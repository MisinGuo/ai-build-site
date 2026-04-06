import sys
import re

sql_path = 'd:\\游戏推广项目\\projects\\game-box\\sql\\gamebox_v3.64.sql'

log_tables = set()
del_flag_tables = set()

current_table = None

try:
    with open(sql_path, 'r', encoding='utf-8') as f:
        for line in f:
            create_match = re.match(r'CREATE TABLE\s+`?([\w_]+)`?\s*\(', line, re.IGNORECASE)
            if create_match:
                current_table = create_match.group(1)
                
                # Check for log tables
                t_lower = current_table.lower()
                if 'log' in t_lower or 'oper' in t_lower or 'logininfor' in t_lower:
                    log_tables.add(current_table)
            
            elif current_table:
                if re.search(r'`?(del_flag|deleted)`?\s+(char|tinyint|int)', line, re.IGNORECASE):
                    del_flag_tables.add(current_table)
                
                if line.strip().startswith(')') and ('ENGINE' in line or ';' in line):
                    current_table = None
                    
except Exception as e:
    print(f"Error: {e}")
    sys.exit(1)

print("Tables with del_flag:")
for t in sorted(list(del_flag_tables)):
    print(t)
    
print("\nLog tables:")
for t in sorted(list(log_tables)):
    print(t)
