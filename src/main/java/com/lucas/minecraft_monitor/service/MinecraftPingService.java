package com.lucas.minecraft_monitor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucas.minecraft_monitor.dto.MinecraftPingResponse;
import com.lucas.minecraft_monitor.exception.MinecraftServerConnectionException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

@Service
public class MinecraftPingService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MinecraftPingResponse ping(String host, int port) {

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(host, port),
                    5000
            );

            socket.setSoTimeout(5000);

            DataOutputStream output =
                    new DataOutputStream(
                            socket.getOutputStream()
                    );

            DataInputStream input =
                    new DataInputStream(
                            socket.getInputStream()
                    );

            sendHandshake(output, host, port);

            sendStatusRequest(output);

            readVarInt(input);

            int packetId = readVarInt(input);

            if (packetId != 0x00) {
                throw new MinecraftServerConnectionException(
                        "Packet ID inesperado: " + packetId
                );
            }

            int jsonLength = readVarInt(input);

            if (jsonLength <= 0) {
                throw new MinecraftServerConnectionException(
                        "O servidor retornou uma resposta vazia."
                );
            }

            byte[] jsonBytes =
                    new byte[jsonLength];

            input.readFully(jsonBytes);

            String json =
                    new String(
                            jsonBytes,
                            StandardCharsets.UTF_8
                    );

            return parseResponse(json);

        } catch (SocketTimeoutException e) {

            throw new MinecraftServerConnectionException(
                    "Timeout ao conectar com o servidor.",
                    e
            );

        } catch (ConnectException e) {

            throw new MinecraftServerConnectionException(
                    "Não foi possível conectar ao servidor.",
                    e
            );

        } catch (IOException e) {

            throw new MinecraftServerConnectionException(
                    "Erro de comunicação com o servidor Minecraft.",
                    e
            );
        }
    }

    private void sendHandshake(
            DataOutputStream output,
            String host,
            int port
    ) throws IOException {

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        DataOutputStream data =
                new DataOutputStream(buffer);

        // Packet ID
        writeVarInt(data, 0x00);

        // Protocol version
        writeVarInt(data, 0);

        // Host
        writeString(data, host);

        // Port
        data.writeShort(port);

        // Next state: Status
        writeVarInt(data, 0x01);

        data.flush();

        byte[] packetData =
                buffer.toByteArray();

        writeVarInt(
                output,
                packetData.length
        );

        output.write(packetData);
        output.flush();
    }

    private void sendStatusRequest(
            DataOutputStream output
    ) throws IOException {

        // Packet length
        writeVarInt(output, 1);

        // Packet ID
        writeVarInt(output, 0x00);

        output.flush();
    }

    private MinecraftPingResponse parseResponse(
            String json
    ) throws IOException {

        JsonNode root =
                objectMapper.readTree(json);

        if (root == null || root.isEmpty()) {
            throw new IOException(
                    "Resposta JSON inválida."
            );
        }

        JsonNode players =
                root.path("players");

        int playersOnline =
                players.path("online").asInt(0);

        int maxPlayers =
                players.path("max").asInt(0);

        String version =
                root.path("version")
                        .path("name")
                        .asText(null);

        String motd =
                extractMotd(
                        root.path("description")
                );

        return new MinecraftPingResponse(
                playersOnline,
                maxPlayers,
                version,
                motd
        );
    }

    private String extractMotd(
            JsonNode description
    ) {

        if (description == null ||
                description.isMissingNode() ||
                description.isNull()) {

            return null;
        }

        if (description.isTextual()) {
            return description.asText();
        }

        if (description.isObject()) {

            StringBuilder result =
                    new StringBuilder();

            JsonNode text =
                    description.get("text");

            if (text != null) {
                result.append(text.asText());
            }

            JsonNode extra =
                    description.get("extra");

            if (extra != null &&
                    extra.isArray()) {

                for (JsonNode component : extra) {

                    JsonNode componentText =
                            component.get("text");

                    if (componentText != null) {
                        result.append(
                                componentText.asText()
                        );
                    }
                }
            }

            return result.toString();
        }

        return null;
    }

    private void writeString(
            DataOutputStream output,
            String value
    ) throws IOException {

        byte[] bytes =
                value.getBytes(
                        StandardCharsets.UTF_8
                );

        writeVarInt(
                output,
                bytes.length
        );

        output.write(bytes);
    }

    private void writeVarInt(
            DataOutputStream output,
            int value
    ) throws IOException {

        while ((value & 0xFFFFFF80) != 0) {

            output.writeByte(
                    (value & 0x7F) | 0x80
            );

            value >>>= 7;
        }

        output.writeByte(value);
    }

    private int readVarInt(
            DataInputStream input
    ) throws IOException {

        int value = 0;
        int position = 0;

        while (true) {

            int currentByte =
                    input.readUnsignedByte();

            value |=
                    (currentByte & 0x7F)
                            << position;

            if ((currentByte & 0x80) == 0) {
                break;
            }

            position += 7;

            if (position >= 35) {
                throw new IOException(
                        "VarInt muito grande."
                );
            }
        }

        return value;
    }
}