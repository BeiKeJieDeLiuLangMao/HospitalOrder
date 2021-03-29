import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)
const moment = require('moment')
const today = new Date()
export default new Vuex.Store({
  state: {
    connected: false,
    loginLoading: false,
    departments: [],
    doctors: {},
    specs: {},
    user: {},
    userInfo: {},
    medCards: [],
    initedCount: 5,
    departmentIndex: localStorage.getItem('departmentIndex') === null ? 0 : Number(localStorage.getItem('departmentIndex')),
    outpatientTypeIndex: localStorage.getItem('outpatientTypeIndex') === null ? 0 : Number(localStorage.getItem('outpatientTypeIndex')),
    today,
    selectedDate: moment(today).format('YYYY-MM-DD'),
    items: [],
    loadedItems: false,
    order: undefined
  },
  mutations: {
    setConnected (state, connected) {
      state.connected = connected
    },
    setDepartments (state, departments) {
      state.departments = departments
      state.initedCount--
    },
    setDoctors (state, doctors) {
      for (let index in doctors) {
        if (doctors.hasOwnProperty(index)) {
          state.doctors[doctors[index].doctorCode] = doctors[index]
        }
      }
      state.initedCount--
    },
    setUser (state, user) {
      state.user = user
    },
    setSpecs (state, specs) {
      for (let index in specs) {
        if (specs.hasOwnProperty(index)) {
          state.specs[specs[index].specCode] = specs[index]
        }
      }
      state.initedCount--
    },
    setUserInfo (state, userInfo) {
      state.userInfo = userInfo
      state.initedCount--
    },
    setMedCards (state, medCards) {
      state.medCards = medCards
      state.initedCount--
    },
    setItems (state, items) {
      state.items = items
      state.loadedItems = true
    },
    setOrder (state, order) {
      state.order = order
    },
    resetInitedCount (state) {
      state.initedCount = 5
    },
    setLoginLoading (state, status) {
      state.loginLoading = status
    },
    setDepartmentIndex (state, index) {
      state.departmentIndex = index
      localStorage.setItem('departmentIndex', index)
    },
    setOutpatientTypeIndex (state, index) {
      state.outpatientTypeIndex = index
      localStorage.setItem('outpatientTypeIndex', index)
    },
    setSelectedDate (state, date) {
      state.selectedDate = date
    }
  },
  getters: {
    isConnected: state => {
      return state.connected
    },
    getDepartments: state => {
      return state.departments
    },
    getDoctors: state => {
      return state.doctors
    },
    getUser: state => {
      return state.user
    },
    getOs: state => {
      let ua = navigator.userAgent
      let isWindowsPhone = /(?:Windows Phone)/.test(ua)
      let isSymbian = /(?:SymbianOS)/.test(ua) || isWindowsPhone
      let isAndroid = /(?:Android)/.test(ua)
      let isFireFox = /(?:Firefox)/.test(ua)
      let isTablet = /(?:iPad|PlayBook)/.test(ua) || (isAndroid && !/(?:Mobile)/.test(ua)) || (isFireFox && /(?:Tablet)/.test(ua))
      let isPhone = /(?:iPhone)/.test(ua) && !isTablet
      let isPc = !isPhone && !isAndroid && !isSymbian
      return {
        isTablet: isTablet,
        isPhone: isWindowsPhone || isAndroid || isPhone,
        isAndroid: isAndroid,
        isPc: isPc,
        isAlipay: navigator.userAgent.toLowerCase().indexOf('alipay') !== -1
      }
    },
    getSpecs: state => {
      return state.specs
    },
    getUserInfo: state => {
      return state.userInfo
    },
    getMedCards: state => {
      return state.medCards
    },
    getItems: state => {
      return state.items
    },
    getOrder: state => {
      return state.order
    },
    getInitedCount: state => {
      return state.initedCount
    },
    getLoginLoading: state => {
      return state.loginLoading
    },
    getDepartmentIndex: state => {
      return state.departmentIndex
    },
    getOutpatientTypeIndex: state => {
      return state.outpatientTypeIndex
    },
    getToday: state => {
      return state.today
    },
    getSelectedDate: state => {
      return state.selectedDate
    },
    getLoadedItems: state => {
      return state.loadedItems
    }
  },
  actions: {
    setConnected ({ commit }, connected) {
      commit('setConnected', connected)
    },
    setDepartments ({ commit }, departments) {
      commit('setDepartments', departments)
    },
    setDoctors ({ commit }, doctors) {
      commit('setDoctors', doctors)
    },
    setUser ({ commit }, user) {
      commit('setUser', user)
    },
    setSpecs ({ commit }, specs) {
      commit('setSpecs', specs)
    },
    setItems ({ commit }, items) {
      commit('setItems', items)
    },
    setUserInfo ({ commit }, userInfo) {
      commit('setUserInfo', userInfo)
    },
    setMedCards ({ commit }, medCards) {
      commit('setMedCards', medCards)
    },
    setOrder ({ commit }, order) {
      commit('setOrder', order)
    },
    resetInitedCount ({ commit }) {
      commit('resetInitedCount')
    },
    setLoginLoading ({ commit }, status) {
      commit('setLoginLoading', status)
    },
    setDepartmentIndex ({ commit }, index) {
      commit('setDepartmentIndex', index)
    },
    setOutpatientTypeIndex ({ commit }, index) {
      commit('setOutpatientTypeIndex', index)
    },
    setSelectedDate ({ commit }, date) {
      commit('setSelectedDate', date)
    }
  }
})
