module.exports = {
  productionSourceMap: true,

  transpileDependencies: ['vuetify'],

  pwa: {
    name: 'Medicare Application',
    themeColor: '#517bbd',
    msTileColor: '#000000',
    appleMobileWebAppCapable: 'yes',
    appleMobileWebAppStatusBarStyle: 'black',
    manifestPath: 'manifest.json',
    iconPaths: {
      faviconSVG: 'api/themes/manifest/favicon/_152x152.png',
      favicon32: 'api/themes/manifest/favicon/_32x32.png',
      favicon16: 'api/themes/manifest/favicon/_16x16.png',
      appleTouchIcon: 'api/themes/manifest/favicon/_152x152.png',
      maskIcon: 'api/themes/manifest/favicon/_144x144.png',
      msTileImage: 'api/themes/manifest/favicon/_144x144.png'
    },
    workboxPluginMode: 'InjectManifest',
    workboxOptions: {
      swSrc: 'src/service-worker-source.js'
    }
  },

  pluginOptions: {
    i18n: {
      locale: 'en',
      fallbackLocale: 'en',
      localeDir: 'locales',
      enableInSFC: true,
      enableBridge: false,
      enableLegacy: false,
      runtimeOnly: false,
      compositionOnly: false,
      fullInstall: false
    }
  }
}
