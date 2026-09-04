import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

function ChartCard({
  title,
  data,
  dataKey,
  name,
  allowDecimals = true,
}) {
  return (
    <div className="chart-card">
      <h3>{title}</h3>

      {data.length === 0 ? (
        <div className="chart-empty">
          Nenhum dado disponível.
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={280}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />

            <XAxis dataKey="time" />

            <YAxis allowDecimals={allowDecimals} />

            <Tooltip />

            <Line
              type="monotone"
              dataKey={dataKey}
              name={name}
              strokeWidth={2}
              dot={false}
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}

export default ChartCard;
