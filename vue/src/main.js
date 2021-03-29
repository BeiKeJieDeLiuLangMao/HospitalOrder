import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import './registerServiceWorker'
import VueSocketIO from 'vue-socket.io'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import Vant from 'vant'
import 'vant/lib/index.css'

Vue.use(Vant)
Vue.config.productionTip = false
Vue.use(ElementUI)

if (store.getters.getOs.isAlipay || process.env.NODE_ENV === 'development') {
  Vue.use(new VueSocketIO({
    debug: true,
    connection: window.location.hostname + ':29001',
    vuex: {
      store,
      actionPrefix: 'SOCKET_',
      mutationPrefix: 'SOCKET_'
    }
  }))
}
new Vue({
  router,
  store,
  render: h => h(App),
  sockets: {
    connect: async () => {
      await store.dispatch('setConnected', true)
    },
    disconnect: async () => {
      await store.dispatch('setConnected', false)
    }
  }
}).$mount('#app')
