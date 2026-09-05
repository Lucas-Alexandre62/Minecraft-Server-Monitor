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
            <CartesianGrid strokeDasharray="3 3" stroke="#2a2e3a" />

            <XAxis
              dataKey="time"
              tick={{ fill: '#5c6478', fontSize: 11 }}
              axisLine={{ stroke: '#2a2e3a' }}
              tickLine={{ stroke: '#2a2e3a' }}
            />

            <YAxis
              allowDecimals={allowDecimals}
              tick={{ fill: '#5c6478', fontSize: 11 }}
              axisLine={{ stroke: '#2a2e3a' }}
              tickLine={{ stroke: '#2a2e3a' }}
            />

            <Tooltip
              contentStyle={{
                background: '#1e212b',
                border: '2px solid #2a2e3a',
                borderRadius: '4px',
                fontSize: '12px',
                color: '#e2e5ec',
              }}
            />

            <Line
              type="monotone"
              dataKey={dataKey}
              name={name}
              strokeWidth={2}
              dot={false}
              stroke="#6ee7b7"
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}

export default ChartCard;
