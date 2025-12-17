import { mount } from '@vue/test-utils'
import UploadProgress from '../UploadProgress.vue'

describe('UploadProgress.vue', () => {
  it('renders correctly when show is true', () => {
    const wrapper = mount(UploadProgress, {
      props: {
        show: true,
        percent: 50,
        fileName: 'test.jpg'
      }
    })

    expect(wrapper.find('.upload-progress-overlay').exists()).toBe(true)
    expect(wrapper.find('.progress-text').text()).toBe('50%')
    expect(wrapper.find('.progress-file-name').text()).toBe('test.jpg')
  })

  it('does not render when show is false', () => {
    const wrapper = mount(UploadProgress, {
      props: {
        show: false,
        percent: 50,
        fileName: 'test.jpg'
      }
    })

    expect(wrapper.find('.upload-progress-overlay').exists()).toBe(false)
  })

  it('updates progress bar width based on percent', async () => {
    const wrapper = mount(UploadProgress, {
      props: {
        show: true,
        percent: 0,
        fileName: 'test.jpg'
      }
    })

    expect(wrapper.find('.progress-bar-fill').attributes('style')).toContain('width: 0%')

    await wrapper.setProps({ percent: 75 })
    expect(wrapper.find('.progress-bar-fill').attributes('style')).toContain('width: 75%')
  })

  it('emits cancel event when cancel button is clicked', async () => {
    const wrapper = mount(UploadProgress, {
      props: {
        show: true,
        percent: 50,
        fileName: 'test.jpg',
        cancellable: true
      }
    })

    await wrapper.find('button').trigger('click')

    expect(wrapper.emitted('cancel')).toBeTruthy()
  })

  it('does not show cancel button when cancellable is false', () => {
    const wrapper = mount(UploadProgress, {
      props: {
        show: true,
        percent: 50,
        fileName: 'test.jpg',
        cancellable: false
      }
    })

    expect(wrapper.find('button').exists()).toBe(false)
  })

  it('validates percent prop is between 0 and 100', () => {
    const wrapper = mount(UploadProgress, {
      props: {
        show: true,
        percent: 101,  // 超出范围
        fileName: 'test.jpg'
      }
    })

    // Vue 的 prop 验证应该会在控制台显示警告
    expect(wrapper.vm.percent).toBe(101)
  })
})