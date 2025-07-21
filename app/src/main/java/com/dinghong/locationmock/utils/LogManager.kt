package com.dinghong.locationmock.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日志管理器
 * 负责应用内日志的收集、存储和管理
 */
class LogManager private constructor() {
    
    companion object {
        private const val TAG = "LogManager"
        private const val MAX_LOGS_IN_MEMORY = 200
        private const val LOG_FILE_NAME = "dinghong_debug.log"
        
        @Volatile
        private var INSTANCE: LogManager? = null
        
        fun getInstance(): LogManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LogManager().also { INSTANCE = it }
            }
        }
    }
    
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    /**
     * 日志条目数据类
     */
    data class LogEntry(
        val timestamp: Long,
        val level: LogLevel,
        val tag: String,
        val message: String,
        val throwable: Throwable? = null
    ) {
        fun getFormattedTime(): String {
            return SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(timestamp))
        }
        
        fun getFormattedMessage(): String {
            val timeStr = getFormattedTime()
            val levelStr = level.name
            return "[$timeStr] [$levelStr] [$tag] $message"
        }
    }
    
    /**
     * 日志级别枚举
     */
    enum class LogLevel {
        VERBOSE, DEBUG, INFO, WARN, ERROR
    }
    
    /**
     * 添加日志
     */
    fun addLog(level: LogLevel, tag: String, message: String, throwable: Throwable? = null) {
        val logEntry = LogEntry(
            timestamp = System.currentTimeMillis(),
            level = level,
            tag = tag,
            message = message,
            throwable = throwable
        )
        
        val currentLogs = _logs.value.toMutableList()
        currentLogs.add(0, logEntry) // 添加到顶部
        
        // 限制内存中的日志数量
        if (currentLogs.size > MAX_LOGS_IN_MEMORY) {
            currentLogs.removeAt(currentLogs.size - 1)
        }
        
        _logs.value = currentLogs
        
        // 同时输出到系统日志
        outputToSystemLog(level, tag, message, throwable)
    }
    
    /**
     * 便捷方法：添加不同级别的日志
     */
    fun v(tag: String, message: String) = addLog(LogLevel.VERBOSE, tag, message)
    fun d(tag: String, message: String) = addLog(LogLevel.DEBUG, tag, message)
    fun i(tag: String, message: String) = addLog(LogLevel.INFO, tag, message)
    fun w(tag: String, message: String) = addLog(LogLevel.WARN, tag, message)
    fun e(tag: String, message: String, throwable: Throwable? = null) = addLog(LogLevel.ERROR, tag, message, throwable)
    
    /**
     * 清除所有日志
     */
    fun clearLogs() {
        _logs.value = emptyList()
        i(TAG, "日志已清除")
    }
    
    /**
     * 获取格式化的日志文本
     */
    fun getFormattedLogs(): String {
        return _logs.value.joinToString("\n") { it.getFormattedMessage() }
    }
    
    /**
     * 保存日志到文件
     */
    fun saveLogsToFile(context: Context): Boolean {
        return try {
            val logDir = File(context.getExternalFilesDir(null), "logs")
            if (!logDir.exists()) {
                logDir.mkdirs()
            }
            
            val fileName = "${fileNameFormat.format(Date())}_$LOG_FILE_NAME"
            val logFile = File(logDir, fileName)
            
            FileWriter(logFile, true).use { writer ->
                writer.write("=== 定红定位模拟器调试日志 ===\n")
                writer.write("生成时间: ${dateFormat.format(Date())}\n")
                writer.write("应用版本: ${getAppVersion(context)}\n")
                writer.write("设备信息: ${getDeviceInfo()}\n")
                writer.write("=====================================\n\n")
                
                _logs.value.reversed().forEach { logEntry ->
                    writer.write("${logEntry.getFormattedMessage()}\n")
                    logEntry.throwable?.let { throwable ->
                        writer.write("异常堆栈: ${Log.getStackTraceString(throwable)}\n")
                    }
                }
                
                writer.write("\n=== 日志结束 ===\n")
            }
            
            i(TAG, "日志已保存到: ${logFile.absolutePath}")
            true
        } catch (e: Exception) {
            e(TAG, "保存日志失败", e)
            false
        }
    }
    
    /**
     * 获取日志统计信息
     */
    fun getLogStatistics(): Map<String, Int> {
        val logs = _logs.value
        return mapOf(
            "总数" to logs.size,
            "错误" to logs.count { it.level == LogLevel.ERROR },
            "警告" to logs.count { it.level == LogLevel.WARN },
            "信息" to logs.count { it.level == LogLevel.INFO },
            "调试" to logs.count { it.level == LogLevel.DEBUG },
            "详细" to logs.count { it.level == LogLevel.VERBOSE }
        )
    }
    
    /**
     * 根据级别过滤日志
     */
    fun getLogsByLevel(level: LogLevel): List<LogEntry> {
        return _logs.value.filter { it.level == level }
    }
    
    /**
     * 根据标签过滤日志
     */
    fun getLogsByTag(tag: String): List<LogEntry> {
        return _logs.value.filter { it.tag == tag }
    }
    
    /**
     * 搜索日志
     */
    fun searchLogs(query: String): List<LogEntry> {
        return _logs.value.filter { 
            it.message.contains(query, ignoreCase = true) || 
            it.tag.contains(query, ignoreCase = true)
        }
    }
    
    /**
     * 输出到系统日志
     */
    private fun outputToSystemLog(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, message, throwable)
            LogLevel.DEBUG -> Log.d(tag, message, throwable)
            LogLevel.INFO -> Log.i(tag, message, throwable)
            LogLevel.WARN -> Log.w(tag, message, throwable)
            LogLevel.ERROR -> Log.e(tag, message, throwable)
        }
    }
    
    /**
     * 获取应用版本信息
     */
    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "未知版本"
        }
    }
    
    /**
     * 获取设备信息
     */
    private fun getDeviceInfo(): String {
        return "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} " +
                "(Android ${android.os.Build.VERSION.RELEASE}, API ${android.os.Build.VERSION.SDK_INT})"
    }
    
    /**
     * 获取内存使用情况
     */
    fun getMemoryInfo(): String {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory() / 1024 / 1024
        val freeMemory = runtime.freeMemory() / 1024 / 1024
        val usedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory() / 1024 / 1024
        
        return "内存使用: ${usedMemory}MB / ${maxMemory}MB (总计: ${totalMemory}MB, 可用: ${freeMemory}MB)"
    }
}
