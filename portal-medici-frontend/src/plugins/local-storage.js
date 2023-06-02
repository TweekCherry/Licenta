const LocalStorage = {
  put: (key, item) => localStorage.setItem(key, JSON.stringify(item)),
  get: (key) => {
    let item = localStorage.getItem(key)
    if (item !== null) {
      item = JSON.parse(item)
    }
    return item
  },
  remove: (key) => localStorage.removeItem(key)
}
export default LocalStorage
