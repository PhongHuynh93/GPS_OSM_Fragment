package dhbk.android.gps_osm_fragment.Help;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Thien Nhan on 4/7/2016.
 */
public class DBConnection extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "map";
    public static final String DATABASE_TABLE = "list_trip";
    public static final int DATABASE_VERSION = 8;

    public DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table = "create table " + DATABASE_TABLE +
                "(id integer primary key AUTOINCREMENT , name text, description text,route text ,image text,time text)";
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS list_trip");
        onCreate(db);
    }

    public void addTrip(RouteInfo info) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", info.getName());
        values.put("description", info.getDescription());
        values.put("image", info.getImg());
        values.put("route", info.getRoute());
        values.put("time", info.getTime());
        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }

    public List<RouteInfo> getAllList() {
        List<RouteInfo> list = new ArrayList<RouteInfo>();
        String sql = "SELECT * FROM " + DATABASE_TABLE + " ORDER BY time DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                RouteInfo route = new RouteInfo();
                route.setId(cursor.getString(0));
                route.setName(cursor.getString(1));
                route.setDescription(cursor.getString(2));
                route.setRoute(cursor.getString(3));
                route.setImg(cursor.getString(4));
                route.setTime(cursor.getString(5));

                list.add(route);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public void updateInfO(RouteInfo info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", info.getName());
        values.put("description", info.getDescription());
        values.put("image", info.getImg());
        values.put("route", info.getRoute());
        values.put("time", info.getTime());
        String[] whereArgs = {info.getId()};
        db.update(DATABASE_TABLE, values, "id = ?", whereArgs);
        db.close();
    }

    public RouteInfo getTripInfo(String id) {
        String sql = "SELECT * FROM " + DATABASE_TABLE + " WHERE ID= " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        RouteInfo route = new RouteInfo();
        if (cursor.moveToFirst()) {
            do {
                // RouteInfo route = new RouteInfo();
                route.setId(cursor.getString(0));
                route.setName(cursor.getString(1));
                route.setDescription(cursor.getString(2));
                route.setRoute(cursor.getString(3));
                route.setImg(cursor.getString(4));
                route.setTime(cursor.getString(5));
            } while (cursor.moveToNext());
        }
        db.close();
        return route;
    }
}
