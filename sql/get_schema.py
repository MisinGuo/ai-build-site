import re
sql_path = r'd:\游戏推广项目\projects\game-box\sql\gamebox_v3.64.sql'
with open(sql_path, 'r', encoding='utf-8') as f:
    in_table = False
    for line in f:
        if 'CREATE TABLE `gb_site_ai_platform_relations`' in line:
            in_table = True
        if in_table:
            print(line.strip())
            if line.strip().startswith(')') and ('ENGINE' in line or ';' in line):
                break
