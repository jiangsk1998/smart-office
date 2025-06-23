import mysql.connector
from datetime import datetime, timedelta
import random
import json
from faker import Faker


fake = Faker('zh_CN')


DB_CONFIG = {
    'host': '192.168.1.111',
    'port': 3306,
    'user': 'project',
    'password': 'project',
    'database': 'project_manager'
}


PASSWORD_HASH = "$2a$10$jb33EpAcBCzhU4R0/8JYUezrAm2UBDhgUoybkdjEupqqcQKUJqSBK"


SYSTEM_USER_ID = 1


PROJECT_STATUS = ["NOT_STARTED", "IN_PROGRESS", "OVERDUE", "COMPLETED"]
PHASE_STATUS = ["NOT_STARTED", "IN_PROGRESS", "COMPLETED", "STOP"]
TASK_STATUS = ["NOT_STARTED", "IN_PROGRESS", "COMPLETED", "STOP"]


def get_db_connection():
    return mysql.connector.connect(**DB_CONFIG)


def execute_and_commit(cursor, sql, params=None):
    try:
        cursor.execute(sql, params)
    except mysql.connector.Error as err:
        print(f"Error: {err}")
        print(f"SQL: {sql}")
        print(f"Params: {params}")
        raise
    return cursor.lastrowid

def fetch_all(cursor, sql, params=None):
    cursor.execute(sql, params)
    return cursor.fetchall()

def main():
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True) # Use dictionary=True to get results as dicts

        print("--- Fetching existing departments and users ---")

        cursor.execute("SELECT id, name, dept_type FROM tab_department WHERE dept_type IN ('职能科室', '专业科室')")
        leaf_departments = cursor.fetchall()

        if not leaf_departments:
            print("No leaf departments found. Please ensure 'tab_department' is populated.")
            return


        cursor.execute("SELECT user_id, user_name, department_id FROM tab_user_info")
        all_users = cursor.fetchall()


        users_by_dept = {}
        for user in all_users:
            dept_id = user.get('department_id')
            if dept_id:
                if dept_id not in users_by_dept:
                    users_by_dept[dept_id] = []
                users_by_dept[dept_id].append(user)


        for dept in leaf_departments:
            if dept['id'] not in users_by_dept or not users_by_dept[dept['id']]:
                print(f"Warning: Department '{dept['name']}' (ID: {dept['id']}) has no users. Projects will not be generated for it.")

        print("--- Starting data generation and insertion ---")

        total_projects = 0
        total_phases = 0
        total_tasks = 0


        start_date_range = datetime(2025, 6, 1).date()
        end_date_range = datetime(2025, 12, 31).date()


        total_days_in_range = (end_date_range - start_date_range).days + 1

        for dept in leaf_departments:
            dept_id = dept['id']
            dept_name = dept['name']
            dept_type = dept['dept_type']

            current_dept_users = users_by_dept.get(dept_id, [])
            if not current_dept_users:
                continue


            responsible_leader_user = next((u for u in current_dept_users if '领导' in u['user_name']), None)
            technical_leader_user = next((u for u in current_dept_users if '技术' in u['user_name']), None)
            plan_supervisors_users = [u for u in current_dept_users if '主管' in u['user_name']]


            if not responsible_leader_user and current_dept_users:
                responsible_leader_user = random.choice(current_dept_users)
            if not technical_leader_user and current_dept_users:
                technical_leader_user = random.choice(current_dept_users)

            if not plan_supervisors_users and current_dept_users:

                plan_supervisors_users = random.sample(current_dept_users, min(3, len(current_dept_users)))


            if not responsible_leader_user or not technical_leader_user or not plan_supervisors_users:
                print(f"Warning: Department '{dept_name}' (ID: {dept_id}) lacks sufficient users for responsible roles. Skipping project generation for this department.")
                continue


            num_projects_for_dept = 10 if dept_type == '专业科室' else 1

            if dept_type == '专业科室' and len(current_dept_users) < 5:
                num_projects_for_dept = max(1, len(current_dept_users) // 2)

            print(f"Generating {num_projects_for_dept} projects for department: {dept_name}")

            for i in range(num_projects_for_dept):

                proj_start_day_offset = random.randint(0, total_days_in_range // 2)
                proj_start_date = start_date_range + timedelta(days=proj_start_day_offset)


                min_duration = timedelta(days=30)
                max_duration = min(timedelta(days=180), end_date_range - proj_start_date)

                if max_duration.days < min_duration.days:
                    proj_end_date = proj_start_date + min_duration
                    if proj_end_date > end_date_range:
                        proj_end_date = end_date_range
                        proj_start_date = proj_end_date - min_duration
                else:
                    duration_days = random.randint(min_duration.days, max_duration.days)
                    proj_end_date = proj_start_date + timedelta(days=duration_days)


                current_date = datetime.now().date()
                if random.random() < 0.25:
                    past_days_offset = random.randint(1, 90)
                    proj_end_date = current_date - timedelta(days=past_days_offset)

                    proj_start_date = proj_end_date - timedelta(days=random.randint(15, 60))
                    if proj_start_date < datetime(2025, 1, 1).date(): # Avoid dates too far in past
                        proj_start_date = datetime(2025, 1, 1).date()


                project_status = "NOT_STARTED"
                if proj_end_date < current_date:
                    project_status = random.choice(["OVERDUE", "COMPLETED"])
                elif proj_start_date <= current_date <= proj_end_date:
                    project_status = "IN_PROGRESS"

                project_number = f"{dept_name[:2].upper()}-{fake.unique.random_number(digits=5)}"
                project_name = f"{dept_name} {fake.catch_phrase()} 项目 {i+1}"

                creator = random.choice(current_dept_users)
                updater = random.choice(current_dept_users)


                plan_supervisors_json = json.dumps([{"userId": u['user_id'], "userName": u['user_name']} for u in plan_supervisors_users])
                project_participants = ", ".join([u['user_name'] for u in current_dept_users])

                sql_project = """
                INSERT INTO tab_project_info (project_number, project_name, department, start_date, end_date, 
                                              status, current_phase, responsible_leader_id, technical_leader_id, 
                                              responsible_leader, technical_leader, plan_supervisors, project_participants, 
                                              is_favorite, creator_id, creator_name, create_time, updater_id, updater_name, update_time)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                """
                project_id = execute_and_commit(cursor, sql_project, (
                    project_number, project_name, dept_name, proj_start_date, proj_end_date,
                    project_status, "初期阶段", responsible_leader_user['user_id'], technical_leader_user['user_id'],
                    responsible_leader_user['user_name'], technical_leader_user['user_name'], plan_supervisors_json, project_participants,
                    random.choice([True, False]), creator['user_id'], creator['user_name'], datetime.now(), updater['user_id'], updater['user_name'], datetime.now()
                ))
                total_projects += 1

                num_phases = random.randint(10, 20)
                for j in range(num_phases):

                    phase_start_date = fake.date_between(start_date=proj_start_date, end_date=proj_end_date)
                    phase_end_date = fake.date_between(start_date=phase_start_date, end_date=proj_end_date)

                    phase_status = "NOT_STARTED"
                    if phase_end_date < current_date:
                        phase_status = random.choice(["COMPLETED", "STOP"])
                    elif phase_start_date <= current_date <= phase_end_date:
                        phase_status = "IN_PROGRESS"

                    phase_name = f"阶段 {j+1}: {fake.word()}规划"
                    phase_responsible_person = random.choice(current_dept_users)['user_name']
                    deliverable = fake.file_name() # Removed 'category' argument
                    deliverable_type = random.choice(["文档", "原型", "代码", "报告"])

                    sql_phase = """
                    INSERT INTO tab_project_phase (project_id, phase_name, phase_status, start_date, end_date,
                                                   responsible_person, department, deliverable, deliverable_type,
                                                   create_time, update_time)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    """
                    phase_id = execute_and_commit(cursor, sql_phase, (
                        project_id, phase_name, phase_status, phase_start_date, phase_end_date,
                        phase_responsible_person, dept_name, deliverable, deliverable_type,
                        datetime.now(), datetime.now()
                    ))
                    total_phases += 1

                    num_tasks = random.randint(5, 15) # At least 5 tasks
                    for k in range(num_tasks):

                        task_start_date = fake.date_between(start_date=phase_start_date, end_date=phase_end_date)
                        task_end_date = fake.date_between(start_date=task_start_date, end_date=phase_end_date)

                        task_status = "NOT_STARTED"
                        real_start_date = None
                        real_end_date = None

                        if task_end_date < current_date:
                            task_status = random.choice(["COMPLETED", "STOP"])
                            if task_status == "COMPLETED":

                                real_start_date = fake.date_between(start_date=task_start_date, end_date=task_end_date)
                                real_end_date = fake.date_between(start_date=real_start_date, end_date=task_end_date)
                        elif task_start_date <= current_date <= task_end_date:
                            task_status = "IN_PROGRESS"

                        task_description = f"任务 {k+1}: {fake.sentence(nb_words=6)}"
                        task_responsible_person = random.choice(current_dept_users)['user_name']
                        task_deliverable = fake.file_name() # Removed 'category' argument
                        task_deliverable_type = random.choice(["文档", "演示", "代码片段"])

                        sql_task = """
                        INSERT INTO tab_project_plan (project_id, phase_id, is_top, task_package, task_description, 
                                                      start_date, end_date, responsible_person, department, 
                                                      deliverable, deliverable_type, is_milestone, 
                                                      create_time, update_time, task_status, real_start_date, real_end_date)
                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                        """
                        execute_and_commit(cursor, sql_task, (
                            project_id, phase_id, random.choice([True, False]), phase_name, task_description,
                            task_start_date, task_end_date, task_responsible_person, dept_name,
                            task_deliverable, task_deliverable_type, random.choice([True, False]),
                            datetime.now(), datetime.now(), task_status, real_start_date, real_end_date
                        ))
                        total_tasks += 1

        conn.commit()
        print("\n--- Data insertion complete ---")
        print(f"Total projects inserted: {total_projects}")
        print(f"Total phases inserted: {total_phases}")
        print(f"Total tasks inserted: {total_tasks}")

    except mysql.connector.Error as err:
        print(f"Database error: {err}")
        if conn:
            conn.rollback() # Rollback on error
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        if conn:
            conn.rollback()
    finally:
        if conn:
            cursor.close()
            conn.close()
            print("Database connection closed.")

if __name__ == "__main__":
    main()