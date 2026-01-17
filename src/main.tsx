/**
 * 粤语评书应用程序入口文件
 * 
 * 功能：
 * - 初始化React应用程序
 * - 挂载根组件到DOM
 * - 应用全局CSS样式
 * 
 * 技术栈：
 * - React 18 (createRoot API)
 * - TypeScript
 * - Tauri (桌面应用框架)
 */

import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './index.css'

/**
 * 应用程序入口点
 * 在严格模式下渲染粤语评书主应用组件
 */
ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    {/* 粤语评书主应用组件 */}
    <App />
  </React.StrictMode>,
)