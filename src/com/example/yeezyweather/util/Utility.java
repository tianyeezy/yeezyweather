package com.example.yeezyweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.yeezyweather.model.City;
import com.example.yeezyweather.model.County;
import com.example.yeezyweather.model.Province;
import com.example.yeezyweather.model.YeezyWeatherDB;

public class Utility {
	/*解析和处理服务器返回的省级数据*/
	public synchronized static boolean handleProvincesResponse(YeezyWeatherDB
		yeezyWeatherDB,String response){
			if(!TextUtils.isEmpty(response)){
				String[] allProvinces=response.split(",");
				if(allProvinces!=null&&allProvinces.length>0){
					for(String p:allProvinces){
						String[] array=p.split("\\|");
						Province province=new Province();
						province.setProvinceCode(array[0]);
						province.setProvinceName(array[1]);
						yeezyWeatherDB.saveProvinnce(province);
					}
					return true;
				}
			}
		return false;
	}
	/*解析和处理服务器返回的市级数据*/
	public static boolean handleCitiesResponse(YeezyWeatherDB
			yeezyWeatherDB,String response,int provinceId){
				if(!TextUtils.isEmpty(response)){
					String[] allCities=response.split(",");
					if(allCities!=null&&allCities.length>0){
						for(String c:allCities){
							String[] array=c.split("\\|");
							City city=new City();
							city.setCityCode(array[0]);
							city.setCityName(array[1]);
							city.setProvinceId(provinceId);
							yeezyWeatherDB.saveCity(city);
						}
						return true;
					}
				}
			return false;
		}
	/*解析和处理服务器返回的县级数据*/
	public static boolean handleCountiesResponse(YeezyWeatherDB yeezyWeatherDB,
			String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties=response.split(",");
			if(allCounties!=null&&allCounties.length>0){
				for(String c:allCounties){
					String[] array=c.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					yeezyWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
	}
	/*将服务器返回的所有天气信息存储到SharePreference文件中*/
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("pubilsh_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
