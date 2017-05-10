package com.Register;


import android.content.*;
import android.database.sqlite.*;
import android.database.Cursor;

/**
 * �û�ע�����ݱ�ע��ʱ��ȡ�û�������Ϣ�������ݱ��У���½ʱ���û���������������е����ݽ��м�Ȩ
 * 
 * @Author ������
 * @Time 2013-10-01
 */
public class UserDAO {

	private DatabaseHelper dbHelper;

	public UserDAO(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	// ��¼ʱ�������username,password��ѯuser���ݱ�����ݽ��м�Ȩ���Ƿ�Ϊ�Ϸ��û���
	public boolean login(String username, String password) {
		// �Զ�д��ʽ�����ݿ⣬����ѯ���ݱ��е�username,password�ֶε�����Ԫ��
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql = "select * from user where username=? and password=?";
		// ʹ���α� (Cursors) �����������ѯ���
		Cursor cursor = sdb.rawQuery(sql, new String[] { username, password });
		// ����α�ָ���һ�У��򷵻� true
		if (cursor.moveToFirst() == true) {
			cursor.close();
			return true;
		}
		return false;
	}

	// ע���û��������Ϣ����user���ݱ�
	public boolean register(User user) {
		// �Զ�д��ʽ�����ݿ⣬���ձ����������
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql = "insert into user(username,password,name,sex,age,phone) values(?,?,?,?,?,?)";
		Object obj[] = { user.getUsername(), user.getPassword(),
				user.getName(), user.getSex(), user.getAge(), user.getPhone() };
		sdb.execSQL(sql, obj);
		return true;
	}

	// ����ϵͳ���û����������ݿ����Ϣ�����
	public String[] readDisplay(String username) {
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql = "select * from user where username=?;";
		Cursor cursor = sdb.rawQuery(sql, new String[] { username });
		cursor.moveToFirst();
		String baseInfo[] = { cursor.getString(1), cursor.getString(2),
				cursor.getString(3), cursor.getString(4), cursor.getString(5),
				cursor.getString(6) };
		return baseInfo;
	}
}
