/**
 * 数据库清理工具
 * 用于清理数据库中不存在的文件记录
 */

/**
 * 检查数据库记录与实际文件的一致性
 * @param {Array} dbFiles - 数据库文件记录
 * @param {Function} getUrlFn - 获取文件URL的函数
 * @returns {Promise<{valid: Array, invalid: Array}>} 有效和无效的文件列表
 */
export const checkDatabaseConsistency = async (dbFiles, getUrlFn = (file) => file.fileUrl) => {
  const result = {
    valid: [],
    invalid: []
  }

  console.log('🔍 开始检查数据库一致性，总文件数:', dbFiles.length)

  for (let i = 0; i < dbFiles.length; i++) {
    const file = dbFiles[i]
    const fileUrl = getUrlFn(file)

    try {
      // 构建文件路径用于测试
      const testUrl = fileUrl.replace(':8081', ':8080').replace(/https?:\/\/localhost:8080/, 'http://localhost:8080')

      const response = await fetch(testUrl, {
        method: 'HEAD',
        cache: 'no-cache',
        mode: 'cors',
        credentials: 'omit'
      })

      if (response.ok) {
        result.valid.push(file)
      } else {
        console.warn(`❌ 无效文件记录: ${fileUrl} (HTTP ${response.status})`)
        result.invalid.push({
          ...file,
          reason: `HTTP ${response.status} ${response.statusText}`
        })
      }
    } catch (error) {
      console.warn(`❌ 无效文件记录: ${fileUrl} (${error.message})`)
      result.invalid.push({
        ...file,
        reason: error.message
      })
    }

    // 显示进度
    if ((i + 1) % 5 === 0 || i === dbFiles.length - 1) {
      console.log(`📊 进度: ${i + 1}/${dbFiles.length}, 有效: ${result.valid.length}, 无效: ${result.invalid.length}`)
    }
  }

  console.log('✅ 数据库一致性检查完成')
  console.log(`📈 统计结果: 有效文件 ${result.valid.length} 个, 无效文件 ${result.invalid.length} 个`)

  return result
}

/**
 * 生成数据库清理报告
 * @param {Object} consistencyResult - 一致性检查结果
 * @param {string} category - 文件分类
 * @returns {Object} 清理报告
 */
export const generateCleanupReport = (consistencyResult, category) => {
  const report = {
    category,
    timestamp: new Date().toISOString(),
    summary: {
      total: consistencyResult.valid.length + consistencyResult.invalid.length,
      valid: consistencyResult.valid.length,
      invalid: consistencyResult.invalid.length,
      cleanupNeeded: consistencyResult.invalid.length > 0
    },
    invalidFiles: consistencyResult.invalid.map(file => ({
      id: file.id,
      url: file.fileUrl,
      fileName: file.originalName,
      reason: file.reason,
      category: file.category
    })),
    recommendations: []
  }

  // 生成建议
  if (report.summary.invalid > 0) {
    report.recommendations.push(
      `需要清理 ${report.summary.invalid} 个无效的 ${category} 文件记录`,
      '建议联系管理员执行数据库清理操作',
      '清理后页面将不再显示无效的文件链接'
    )
  } else {
    report.recommendations.push(
      `${category} 分类的文件记录都是有效的`,
      '无需执行清理操作'
    )
  }

  return report
}

/**
 * 在控制台显示清理报告
 * @param {Object} report - 清理报告
 */
export const displayCleanupReport = (report) => {
  console.group(`🗂️ ${report.category} 文件清理报告`)
  console.log('📅 检查时间:', report.timestamp)
  console.log('📊 统计信息:', report.summary)

  if (report.invalidFiles.length > 0) {
    console.group('❌ 无效文件列表:')
    report.invalidFiles.forEach((file, index) => {
      console.log(`${index + 1}. ID:${file.id} | ${file.fileName} | ${file.reason}`)
    })
    console.groupEnd()
  }

  console.log('💡 建议:')
  report.recommendations.forEach((rec, index) => {
    console.log(`  ${index + 1}. ${rec}`)
  })
  console.groupEnd()
}

export default {
  checkDatabaseConsistency,
  generateCleanupReport,
  displayCleanupReport
}