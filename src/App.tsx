import { useState, useEffect } from 'react'
import { invoke } from '@tauri-apps/api/core'
import { listen } from '@tauri-apps/api/event'

/**
 * 录制状态接口
 * 定义音频录制的各种状态
 */
interface RecordingStatus {
  isRecording: boolean      // 是否正在录制
  isProcessing: boolean     // 是否正在处理（训练模型）
  audioPath?: string        // 音频文件路径
  error?: string            // 错误信息
  duration: number          // 录制时长
  modelId?: string          // 训练成功的模型ID
}

/**
 * 粤语评书主应用组件
 * 
 * 功能：
 * - 录制粤语语音（30秒样本）
 * - 训练个性化的声音模型  
 * - 显示录制进度和状态
 * - 处理错误和异常情况
 */
function App() {
  // 录制状态管理
  const [status, setStatus] = useState<RecordingStatus>({
    isRecording: false,
    isProcessing: false,
    duration: 0
  })

  // 录制计时器（秒）
  const [recordingTime, setRecordingTime] = useState(0)

  /**
   * 录制计时器效果
   * 当开始录制时，每秒更新录制时间
   */
  useEffect(() => {
    let interval: ReturnType<typeof setInterval>
    
    if (status.isRecording) {
      interval = setInterval(() => {
        setRecordingTime(prev => prev + 1)
      }, 1000)
    } else {
      setRecordingTime(0)
    }

    return () => {
      if (interval) clearInterval(interval)
    }
  }, [status.isRecording])

  /**
   * 录制进度监听
   * 监听来自Tauri后端的录制进度事件
   */
  useEffect(() => {
    const unsubscribe = listen('recording-progress', (event) => {
      console.log('录制进度:', event.payload)
    })

    return () => {
      unsubscribe.then(unsub => unsub())
    }
  }, [])

  /**
   * 开始录制音频
   * 调用Tauri命令开始录制30秒粤语语音
   */
  const startRecording = async () => {
    try {
      setStatus(prev => ({ ...prev, isRecording: true, error: undefined }))
      
      // 调用Rust后端开始录制，时长30秒，格式为WAV
      const audioPath = await invoke<string>('start_recording', {
        duration: 30, // MVP版本固定30秒
        format: 'wav'
      })
      
      setStatus(prev => ({ ...prev, audioPath }))
    } catch (error) {
      setStatus(prev => ({ 
        ...prev, 
        isRecording: false, 
        error: error instanceof Error ? error.message : '录制失败'
      }))
    }
  }

  /**
   * 停止录制音频
   * 调用Tauri命令停止当前录制
   */
  const stopRecording = async () => {
    try {
      setStatus(prev => ({ ...prev, isProcessing: true }))
      
      // 调用Rust后端停止录制
      await invoke('stop_recording')
      
      setStatus(prev => ({ 
        ...prev, 
        isRecording: false, 
        isProcessing: false 
      }))
    } catch (error) {
      setStatus(prev => ({ 
        ...prev, 
        isRecording: false, 
        isProcessing: false,
        error: error instanceof Error ? error.message : '停止录制失败'
      }))
    }
  }

  /**
   * 训练声音模型
   * 使用录制的音频训练个性化的粤语声音模型
   */
  const trainVoiceModel = async () => {
    if (!status.audioPath) return

    try {
      setStatus(prev => ({ ...prev, isProcessing: true, error: undefined, modelId: undefined }))

      // 调用Rust后端训练声音模型
      const modelId = await invoke<string>('train_voice_model', {
        audioPath: status.audioPath,
        userId: 1, // MVP版本使用临时用户ID
        dialect: 'cantonese'  // 粤语
      })

      console.log('声音模型训练完成:', modelId)
      setStatus(prev => ({ ...prev, isProcessing: false, modelId }))
    } catch (error) {
      setStatus(prev => ({
        ...prev,
        isProcessing: false,
        error: error instanceof Error ? error.message : '声音训练失败'
      }))
    }
  }

  /**
   * 格式化时间显示
   * 将秒数转换为"分:秒"格式
   * 
   * @param seconds 秒数
   * @returns 格式化的时间字符串
   */
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins}:${secs.toString().padStart(2, '0')}`
  }

  return (
    <div className="container">
      <h1>粤语评书 - Cantonese Storyteller</h1>
      <p>录制您的粤语声音，生成个性化评书音频</p>
      
      <div className="recorder-section">
        <h2>声音录制</h2>
        <p>请用粤语朗读以下文本（约30秒）：</p>
        <div style={{ 
          background: '#f0f0f0', 
          padding: '1rem', 
          borderRadius: '4px',
          margin: '1rem 0',
          textAlign: 'left'
        }}>
          <p><strong>示例文本：</strong></p>
          <p>各位听众朋友，今日我哋嚟講一個好精彩嘅故事。從前有個書生，佢好勤力讀書，每日都唔停咁溫習功課...</p>
        </div>
        
        <button
          className={`record-button ${status.isRecording ? 'recording' : ''}`}
          onClick={status.isRecording ? stopRecording : startRecording}
          disabled={status.isProcessing}
        >
          {status.isRecording ? `停止录制 (${formatTime(recordingTime)})` : '开始录制'}
        </button>

        {status.audioPath && (
          <div className="status">
            <p>✅ 录制完成！音频文件已保存</p>
            <button
              onClick={trainVoiceModel}
              disabled={status.isProcessing}
              style={{ marginTop: '1rem' }}
            >
              {status.isProcessing ? '训练中...' : '训练声音模型'}
            </button>
          </div>
        )}

        {status.modelId && (
          <div className="status" style={{ background: '#e8f5e9', border: '1px solid #4caf50', borderRadius: '4px', padding: '1rem', marginTop: '1rem' }}>
            <p style={{ margin: 0, color: '#2e7d32', fontWeight: 'bold' }}>🎉 声音模型训练成功！</p>
            <p style={{ margin: '0.5rem 0 0 0', fontSize: '0.9rem', fontFamily: 'monospace' }}>模型ID: {status.modelId}</p>
          </div>
        )}

        {status.error && (
          <div className="status error">
            <p>❌ {status.error}</p>
          </div>
        )}
      </div>
    </div>
  )
}

export default App