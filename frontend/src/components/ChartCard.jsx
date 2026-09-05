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
            <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />

            <XAxis
              dataKey="time"
              tick={{ fill: 'var(--text-dim)', fontSize: 11 }}
              axisLine={{ stroke: 'var(--border)' }}
              tickLine={{ stroke: 'var(--border)' }}
            />

            <YAxis
              allowDecimals={allowDecimals}
              tick={{ fill: 'var(--text-dim)', fontSize: 11 }}
              axisLine={{ stroke: 'var(--border)' }}
              tickLine={{ stroke: 'var(--border)' }}
            />

            <Tooltip
              contentStyle={{
                background: 'var(--bg-elevated)',
                border: '2px solid var(--border)',
                borderRadius: '0px',
                fontSize: '12px',
                color: 'var(--text)',
                boxShadow: '3px 3px 0 rgba(0,0,0,0.4)',
              }}
            />

            <Line
              type="monotone"
              dataKey={dataKey}
              name={name}
              strokeWidth={2}
              dot={false}
              stroke="var(--grass-light)"
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}

export default ChartCard;
