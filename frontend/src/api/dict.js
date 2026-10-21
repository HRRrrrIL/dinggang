import request from './request'

// 字典缓存
const dictCache = {}

/**
 * 按类型加载字典项
 * @param {string} type 字典类型
 * @returns {Promise<Array>} 字典项列表
 */
export async function loadDict(type) {
  if (dictCache[type]) {
    return dictCache[type]
  }
  try {
    const res = await request.get(`/dict/${type}`)
    dictCache[type] = res.data || []
    return dictCache[type]
  } catch (e) {
    return []
  }
}

/**
 * 字典值转标签
 */
export function dictLabel(items, value) {
  const item = items.find(i => i.itemValue == value)
  return item ? item.itemLabel : value
}
