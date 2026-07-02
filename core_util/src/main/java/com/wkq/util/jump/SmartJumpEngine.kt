package com.wkq.util.jump

import com.wkq.util.jump.impl.*

/**
 * 智能跳转引擎
 * 管理跳转策略并执行分发
 */
object SmartJumpEngine {

    private val handlers = mutableListOf<JumpHandler>()

    init {
        // 注册所有处理器
        handlers.add(EmbeddedSchemeHandler()) // 优先级最高
        handlers.add(TaobaoJumpHandler())
        handlers.add(JDJumpHandler())
        handlers.add(PDDJumpHandler())
        handlers.add(DouyinJumpHandler())
        handlers.add(IdlefishJumpHandler())
        
        // 按优先级从高到低排序
        handlers.sortByDescending { it.getPriority() }
    }

    /**
     * 根据 URL 获取对应的跳转 Scheme
     */
    fun getScheme(url: String): String? {
        for (handler in handlers) {
            if (handler.canHandle(url)) {
                val scheme = handler.convertToScheme(url)
                if (scheme != null) return scheme
            }
        }
        return null
    }

    /**
     * 动态注册新的处理器
     */
    fun registerHandler(handler: JumpHandler) {
        handlers.add(handler)
        handlers.sortByDescending { it.getPriority() }
    }

    /** 移除指定类型的处理器。 */
    fun unregisterHandler(clazz: Class<out JumpHandler>) {
        handlers.removeAll { it.javaClass == clazz }
    }

    /** 返回当前已注册处理器快照，便于调试或单元测试。 */
    fun getHandlers(): List<JumpHandler> = handlers.toList()
}
