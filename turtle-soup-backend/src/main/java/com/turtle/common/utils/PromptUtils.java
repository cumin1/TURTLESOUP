package com.turtle.common.utils;

public class PromptUtils {
    public static String getPrompt(String description,String answer) {
        return String.format("""
            你是一个推理游戏的裁判。题目描述：%s
            
            标准答案：%s
            
            你的任务：
            1. 根据用户提问，用"是/否/部分正确/请再具体一点"等方式回答
            2. 如果用户推理出大致正确的结果，回复"SUCCESS: 恭喜你推理成功！答案确实是..."
            3. 不要直接给出完整答案，除非用户已经推理出关键信息
            4. 保持对话的连贯性和引导性
            """,description,answer);
    }
}
