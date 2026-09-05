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
            <CartesianGrid strokeDasharray="3 3" stroke="#3e4454" />

            <XAxis
              dataKey="time"
              tick={{ fill: '#505868', fontSize: 11 }}
              axisLine={{ stroke: '#3e4454' }}
              tickLine={{ stroke: '#3e4454' }}
            />

            <YAxis
              allowDecimals={allowDecimals}
              tick={{ fill: '#505868', fontSize: 11 }}
              axisLine={{ stroke: '#3e4454' }}
              tickLine={{ stroke: '#3e4454' }}
            />

            <Tooltip
              contentStyle={{
                background: '#272c38',
                border: '2px solid #3e4454',
                borderRadius: '0px',
                fontSize: '12px',
                color: '#eef0f4',
                boxShadow: '3px 3px 0 rgba(0,0,0,0.5)',
              }}
            />

            <Line
              type="monotone"
              dataKey={dataKey}
              name={name}
              strokeWidth={2}
              dot={false}
              stroke="#86efac"
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}

export default ChartCard;
