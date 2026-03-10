import CryptoJS from 'crypto-js'

const keyStr = '-mall4j-password' // 解密用的key

/**
 * AES 加密密码
 * @param {string} password - 原始密码
 * @returns {string} - 加密后的密码
 */
export function encryptPassword(password) {
  const time = Date.now()
  const key = CryptoJS.enc.Utf8.parse(keyStr)
  const srcs = CryptoJS.enc.Utf8.parse(time + password) // 加密方式: 时间戳 + 密码
  const encrypted = CryptoJS.AES.encrypt(srcs, key, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  })
  return encrypted.toString()
}
