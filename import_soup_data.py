#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
海龟汤数据导入脚本
读取HTML文件中的海龟汤题目数据，并调用API导入到数据库
"""

import requests
import json
import re
from bs4 import BeautifulSoup
import time
import sys

class SoupDataImporter:
    def __init__(self, base_url="http://localhost:8080", username="admin", password="123456"):
        self.base_url = base_url
        self.username = username
        self.password = password
        self.token = None
        self.session = requests.Session()
        
        # 标签映射表
        self.tag_mapping = {
            "经典": 1,
            "悬疑": 2,
            "烧脑": 3,
            "搞笑": 4
        }
        
    def login(self):
        """登录获取JWT token"""
        try:
            login_data = {
                "username": self.username,
                "password": self.password
            }
            
            response = self.session.post(
                f"{self.base_url}/user/login",
                json=login_data,
                headers={"Content-Type": "application/json"}
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("code") == 1:
                    self.token = result.get("data", {}).get("token")
                    print(f"✅ 登录成功，获取到token: {self.token[:20]}...")
                    return True
                else:
                    print(f"❌ 登录失败: {result.get('msg')}")
                    return False
            else:
                print(f"❌ 登录请求失败，状态码: {response.status_code}")
                return False
                
        except Exception as e:
            print(f"❌ 登录异常: {str(e)}")
            return False
    
    def parse_html_data(self, html_file_path):
        """解析HTML文件中的海龟汤数据"""
        try:
            with open(html_file_path, 'r', encoding='utf-8') as f:
                html_content = f.read()
            
            soup = BeautifulSoup(html_content, 'html.parser')
            table = soup.find('table')
            
            if not table:
                print("❌ 未找到数据表格")
                return []
            
            puzzles = []
            rows = table.find_all('tr')[1:]  # 跳过表头
            
            for row in rows:
                cells = row.find_all('td')
                if len(cells) >= 6:
                    # 解析标签
                    tag_text = cells[5].text.strip()
                    tag_ids = []
                    for tag in tag_text.split(','):
                        tag = tag.strip()
                        if tag in self.tag_mapping:
                            tag_ids.append(self.tag_mapping[tag])
                    
                    puzzle = {
                        'id': int(cells[0].text.strip()),
                        'title': cells[1].text.strip(),
                        'description': cells[2].text.strip(),  # 题目描述
                        'answer': cells[3].text.strip(),
                        'difficulty': int(cells[4].text.strip()),
                        'tag_ids': tag_ids
                    }
                    puzzles.append(puzzle)
            
            print(f"✅ 成功解析 {len(puzzles)} 个海龟汤题目")
            return puzzles
            
        except Exception as e:
            print(f"❌ 解析HTML文件失败: {str(e)}")
            return []
    
    def create_soup(self, puzzle):
        """创建单个海龟汤题目"""
        try:
            soup_data = {
                "title": puzzle['title'],
                "description": puzzle['description'],  # 使用description字段
                "answer": puzzle['answer'],
                "difficulty": puzzle['difficulty'],
                "tagIds": puzzle['tag_ids'],  # 使用tagIds字段
                "isPublic": True  # 默认公开
            }
            
            headers = {
                "Content-Type": "application/json",
                "Authorization": f"Bearer {self.token}"
            }
            
            response = self.session.post(
                f"{self.base_url}/soup",
                json=soup_data,
                headers=headers
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("code") == 1:
                    print(f"✅ 成功创建题目 {puzzle['id']}: {puzzle['title']}")
                    return True
                else:
                    print(f"❌ 创建题目 {puzzle['id']} 失败: {result.get('msg')}")
                    return False
            else:
                print(f"❌ 创建题目 {puzzle['id']} 请求失败，状态码: {response.status_code}")
                print(f"响应内容: {response.text}")
                return False
                
        except Exception as e:
            print(f"❌ 创建题目 {puzzle['id']} 异常: {str(e)}")
            return False
    
    def import_all_data(self, html_file_path):
        """导入所有数据"""
        print("🚀 开始导入海龟汤数据...")
        
        # 1. 登录
        if not self.login():
            print("❌ 登录失败，无法继续导入")
            return False
        
        # 2. 解析HTML数据
        puzzles = self.parse_html_data(html_file_path)
        if not puzzles:
            print("❌ 没有解析到数据，无法继续导入")
            return False
        
        # 3. 逐个导入数据
        success_count = 0
        fail_count = 0
        
        for i, puzzle in enumerate(puzzles, 1):
            print(f"\n📝 正在导入第 {i}/{len(puzzles)} 个题目...")
            
            if self.create_soup(puzzle):
                success_count += 1
            else:
                fail_count += 1
            
            # 添加延迟避免请求过快
            time.sleep(0.5)
        
        # 4. 输出结果
        print(f"\n🎉 数据导入完成!")
        print(f"✅ 成功导入: {success_count} 个")
        print(f"❌ 导入失败: {fail_count} 个")
        print(f"📊 总计: {len(puzzles)} 个")
        
        return success_count > 0

def main():
    """主函数"""
    print("🐢 海龟汤数据导入工具")
    print("=" * 50)
    
    # 配置参数
    base_url = "http://localhost:8080"  # Spring Boot服务地址
    username = "turtle1"  # 管理员用户名
    password = "123456"  # 管理员密码
    html_file = "turtle-soup-backend/src/main/resources/templates/soup_data.html"
    
    # 检查HTML文件是否存在
    try:
        with open(html_file, 'r', encoding='utf-8') as f:
            pass
    except FileNotFoundError:
        print(f"❌ HTML文件不存在: {html_file}")
        return
    
    # 创建导入器并执行导入
    importer = SoupDataImporter(base_url, username, password)
    
    try:
        success = importer.import_all_data(html_file)
        if success:
            print("\n🎊 数据导入成功完成！")
        else:
            print("\n💥 数据导入过程中出现错误！")
    except KeyboardInterrupt:
        print("\n⏹️ 用户中断了导入过程")
    except Exception as e:
        print(f"\n💥 导入过程中发生异常: {str(e)}")

if __name__ == "__main__":
    main() 