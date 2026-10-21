<template>
  <div>
    <el-row :gutter="20">
      <!-- 字典类型列表 -->
      <el-col :span="6">
        <el-card>
          <template #header>字典类型</template>
          <div v-for="t in dictTypes" :key="t.type"
               :style="{ padding: '10px', cursor: 'pointer', background: currentType === t.type ? '#ecf5ff' : '' }"
               @click="currentType = t.type; loadItems()">
            {{ t.name }}
          </div>
        </el-card>
      </el-col>

      <!-- 字典项列表 -->
      <el-col :span="18">
        <el-card>
          <template #header>
            <span>字典项 - {{ currentType }}</span>
            <el-button style="float: right" size="small" type="primary" @click="handleAdd">新增</el-button>
          </template>
          <el-table :data="items" border>
            <el-table-column prop="itemValue" label="值" width="100" />
            <el-table-column prop="itemLabel" label="标签" width="150" />
            <el-table-column prop="sort" label="排序" width="80" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const currentType = ref('customer_level')
const items = ref([])

const dictTypes = [
  { type: 'customer_level', name: '客户层级' },
  { type: 'customer_source', name: '客户来源' },
  { type: 'contract_type', name: '合同类型' },
  { type: 'order_type', name: '工单类型' },
  { type: 'urgency', name: '紧急度' },
]

const loadItems = async () => {
  try {
    const res = await request.get(`/dict/${currentType.value}`)
    items.value = res.data || []
  } catch (e) {
    items.value = []
  }
}

const handleAdd = () => {
  // 简化：字典项新增先不做弹窗，演示用
}

onMounted(() => { loadItems() })
</script>
