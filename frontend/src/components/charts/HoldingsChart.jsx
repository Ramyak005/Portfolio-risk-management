import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import Card from '../ui/Card'
import { chartMinWidth, holdingsToChartData } from '../../utils/formatters'

const CHART_COLORS = ['#22d3ee', '#818cf8', '#34d399', '#fbbf24', '#f472b6', '#a78bfa', '#fb7185', '#2dd4bf']

const DISPLAY_LIMIT = 12

function ChartTooltip({ active, payload }) {
  if (!active || !payload?.length) return null
  const { symbol, shares } = payload[0].payload
  return (
    <div className="rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-sm shadow-lg">
      <p className="font-semibold text-white">{symbol}</p>
      <p className="text-slate-400">{shares} shares</p>
    </div>
  )
}

export default function HoldingsChart({ holdings }) {
  const totalCount = Object.keys(holdings ?? {}).length
  const data = holdingsToChartData(holdings, DISPLAY_LIMIT)

  if (!data.length) {
    return (
      <Card title="Holdings" subtitle="Share distribution">
        <p className="py-8 text-center text-sm text-slate-500">No holdings data</p>
      </Card>
    )
  }

  const minWidth = chartMinWidth(data.length)

  return (
    <Card
      title="Holdings"
      subtitle={
        totalCount > DISPLAY_LIMIT
          ? `Top ${DISPLAY_LIMIT} of ${totalCount} positions`
          : 'Share distribution'
      }
    >
      <div className="h-72 w-full overflow-x-auto">
        <div style={{ minWidth, height: '100%' }}>
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={data} margin={{ top: 8, right: 8, left: -8, bottom: 24 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" vertical={false} />
              <XAxis
                dataKey="symbol"
                tick={{ fill: '#94a3b8', fontSize: 11 }}
                axisLine={{ stroke: '#334155' }}
                tickLine={false}
                angle={-35}
                textAnchor="end"
                interval={0}
                height={50}
              />
              <YAxis
                tick={{ fill: '#94a3b8', fontSize: 12 }}
                axisLine={false}
                tickLine={false}
              />
              <Tooltip content={<ChartTooltip />} cursor={{ fill: 'rgba(34, 211, 238, 0.08)' }} />
              <Bar dataKey="shares" radius={[6, 6, 0, 0]} maxBarSize={40}>
                {data.map((entry, index) => (
                  <Cell
                    key={entry.symbol}
                    fill={CHART_COLORS[index % CHART_COLORS.length]}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </Card>
  )
}
