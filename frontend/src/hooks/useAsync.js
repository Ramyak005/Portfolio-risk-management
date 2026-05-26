import { useCallback, useEffect, useState } from 'react'

export function useAsync(asyncFn, deps = []) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const execute = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const result = await asyncFn()
      setData(result)
      return result
    } catch (err) {
      setError(err.message || 'Request failed')
      setData(null)
      throw err
    } finally {
      setLoading(false)
    }
  }, deps)

  useEffect(() => {
    execute().catch(() => {})
  }, [execute])

  return { data, loading, error, refetch: execute }
}
