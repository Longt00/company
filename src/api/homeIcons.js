class HomeIconsAPI {
  constructor() {
    this.baseURL = '/api/public/config'
    this.cache = new Map()
    this.cacheTimeout = 5 * 60 * 1000 // 5分钟缓存
  }

  // 获取首页图标配置（主方式）
  async getHomeIcons() {
    const cacheKey = 'home-icons'
    const cached = this.cache.get(cacheKey)

    // 检查缓存
    if (cached && Date.now() - cached.timestamp < this.cacheTimeout) {
      return cached.data
    }

    try {
      // 尝试获取静态配置文件（使用相对路径，走 Vite 代理）
      const response = await fetch(`${this.baseURL}/home-icons.json`)

      if (response.ok) {
        const data = await response.json()

        // 缓存结果
        this.cache.set(cacheKey, {
          data,
          timestamp: Date.now()
        })

        return data
      }

      // 非 2xx 状态，直接使用本地默认配置
      console.warn('home-icons.json 返回非 2xx 状态，使用默认首页图标配置:', response.status)
      return this.getDefaultHomeIcons()
    } catch (error) {
      // 网络错误或解析错误，同样直接回退本地默认配置
      console.error('获取 home-icons.json 失败，使用默认首页图标配置:', error)
      return this.getDefaultHomeIcons()
    }
  }

  // 获取默认配置
  getDefaultHomeIcons() {
    return {
      title: '首页图标配置',
      description: '用于网站首页展示的4个特色图标',
      icons: [
        {
          position: 1,
          title: 'COMPANY',
          description: '展示公司或团队相关图标',
          iconType: 'icon-company',
          defaultIcon: '/images/default-icons/company.svg',
          customIcon: '',
          enabled: true
        },
        {
          position: 2,
          title: 'TEAM',
          description: '展示团队协作相关图标',
          iconType: 'icon-team',
          defaultIcon: '/images/default-icons/team.svg',
          customIcon: '',
          enabled: true
        },
        {
          position: 3,
          title: 'BEST PRICES',
          description: '展示价格优势相关图标',
          iconType: 'icon-prices',
          defaultIcon: '/images/default-icons/prices.svg',
          customIcon: '',
          enabled: true
        },
        {
          position: 4,
          title: 'QUALITY',
          description: '展示质量保证相关图标',
          iconType: 'icon-quality',
          defaultIcon: '/images/default-icons/quality.svg',
          customIcon: '',
          enabled: true
        }
      ],
      displayOptions: {
        gridColumns: 4,
        showTitles: true,
        showDescriptions: false,
        borderRadius: 8,
        padding: 16,
        gap: 24
      },
      version: '1.0.0',
      lastUpdated: new Date().toISOString().split('T')[0]
    }
  }

  // 清除缓存
  clearCache() {
    this.cache.clear()
  }

  // 预加载图标图片
  preloadIcons(icons) {
    const promises = icons.map(icon => {
      let iconSrc = icon.customIcon || icon.defaultIcon
      if (iconSrc) {
        // 将 /uploads/ 转换为 /api/files/ 相对路径
        if (iconSrc.startsWith('/uploads/')) {
          iconSrc = iconSrc.replace('/uploads/', '/api/files/')
        }
        return new Promise((resolve, reject) => {
          const img = new Image()
          img.onload = resolve
          img.onerror = reject
          img.src = iconSrc
        })
      }
      return Promise.resolve()
    })
    return Promise.all(promises)
  }
}

const homeIconsAPI = new HomeIconsAPI()
export default homeIconsAPI
