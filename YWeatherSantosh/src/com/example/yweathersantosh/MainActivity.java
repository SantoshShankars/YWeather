package com.example.yweathersantosh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
//import com.example.firstonetry.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.*;
import com.facebook.android.Facebook;
import com.facebook.model.*;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class MainActivity extends Activity implements OnClickListener{

	public String image="",link="",feed="",city="",region="",temp="",text="",unitText="";
	public String forecastData[]=new String[20];
	//public String link="";
	//public String feed="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		View goButton = findViewById(R.id.go_button);
		
		goButton.setOnClickListener(this);
		findViewById(R.id.WeatherFB).setOnClickListener(this);
		findViewById(R.id.ForecastFB).setOnClickListener(this);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) 
		{
			case R.id.go_button:
				EditText x= (EditText)findViewById(R.id.url_field);
				RadioGroup tempUnit = (RadioGroup) findViewById(R.id.tempUnit);
				//x.setText("abcd");
				String city = x.getText().toString(); 
				int flag=validate(city);
				if(flag!=0){
					String zipOrCity=(flag==2)?"city":"zipCode";
				
					int selectedId = ((RadioGroup)tempUnit).getCheckedRadioButtonId();
				 
					// find the radiobutton by returned id
				    RadioButton unitTextButton = (RadioButton) findViewById(selectedId);
				    String unit2 = unitTextButton.getText().toString();
				    String unit=unit2.substring(1).toLowerCase();
				//    Toast.makeText(this,zipOrCity, Toast.LENGTH_SHORT).show();
					try {
						city=URLEncoder.encode(city,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String url = "http://cs-server.usc.edu:36607/examples/servlet/HelloWorldExample?cityOrZipCodeText="+city+"&units="+unit+"&cityOrZipCodeSelect="+zipOrCity;
			//		Toast.makeText(this,url, Toast.LENGTH_SHORT).show();
					ThreadTask task = new ThreadTask();
				    task.execute(url);
					
				}
				else
				{
					hideall();
				}
				//getState(x.getText().toString());
				break;
				
			case R.id.ForecastFB:
				//Toast.makeText(this,"Forecast", Toast.LENGTH_SHORT).show();
				
				openForecastDialog();
				break;
				
			case R.id.WeatherFB:
				openWeatherDialog();
				break;
		}
	}
private void hideall()
{
	ImageView img;
	TableLayout weatherTable=(TableLayout)findViewById(R.id.weatherTable);
	LinearLayout fbForecastLayout=(LinearLayout)findViewById(R.id.ForecastFBLayout);
	LinearLayout fbWeatherLayout=(LinearLayout)findViewById(R.id.WeatherFBLayout);
	TextView cityName,regionCountry,weatherText,weatherTemp,fR1Day,fR1Text,fR1High,fR1Low,forecastHeader;
	img=(ImageView)findViewById(R.id.imageWeather);
	cityName=(TextView)findViewById(R.id.cityName);
	regionCountry=(TextView)findViewById(R.id.regionCountry);
	weatherText=(TextView)findViewById(R.id.weatherText);
	weatherTemp=(TextView)findViewById(R.id.weatherTemp);
	forecastHeader=(TextView)findViewById(R.id.forecastHeader);
	cityName.setTextSize(15);
	cityName.setText("Weather Information cannot be Found");
	//if(fbWeatherLayout.getVisibility()==View.INVISIBLE)
		fbWeatherLayout.setVisibility(View.INVISIBLE);
	
	//if(fbForecastLayout.getVisibility()==View.INVISIBLE)
		fbForecastLayout.setVisibility(View.INVISIBLE);
	
	//if(weatherTable.getVisibility()==View.INVISIBLE)
		weatherTable.setVisibility(View.INVISIBLE);
		regionCountry.setVisibility(View.INVISIBLE);//Text(result.resultData[1]+","+result.resultData[2]);
    	weatherText.setVisibility(View.INVISIBLE);
    	weatherTemp.setVisibility(View.INVISIBLE);
    	img.setVisibility(View.INVISIBLE);
    	forecastHeader.setVisibility(View.INVISIBLE);
}
	private void openWeatherDialog() {
		new AlertDialog.Builder(this).setTitle(R.string.postToFB).setItems(R.array.weatherPost,
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface,int i) {
					//startGame(i);
					//Log.d("ddf",i+"");
					if(i==0){
						fblogin(1);
						//publishFeedDialog(1);	
							
					}
					//Toast.makeText(this,i, Toast.LENGTH_SHORT).show();
				}

				}).show();
		
	}

	private void openForecastDialog() {
	

		new AlertDialog.Builder(this).setTitle(R.string.postToFB).setItems(R.array.forecastPost,
		new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialoginterface,int i) {
			//startGame(i);
			//Log.d("ddf",i+"");
			if(i==0){
				fblogin(0);
				//publishFeedDialog(0);	
					
			}
			//Toast.makeText(this,i, Toast.LENGTH_SHORT).show();
		}

		}).show();
	}	
	
	private void publishFeedDialog(int flag) {
		//String xyz=
		//JSONObject prop=new JSONObject();
		Bundle params = new Bundle();
		if(flag==1){
			params.putString("name", city+""+region);
		    params.putString("caption", "The current condition for "+ city+" is "+text);
		    params.putString("description", "Temperature is "+temp+"&deg;"+unitText);
		    params.putString("link", feed);
		    params.putString("picture", image);
		    params.putString("properties", "{\"Look at details \":{ text: 'here', href: \""+link+"\"}}");
		}
	    if(flag==0){
		    params.putString("name", city+""+region);
		    params.putString("caption", "Weather Forecast for "+ city);
		    String desc="";
		    for(int i=0;i<5;i++){
		    	desc=desc+forecastData[i*4+1];
		    	desc=desc+":"+forecastData[i*4];
		    	desc=desc+","+forecastData[i*4+2];
		    	desc=desc+"/"+forecastData[i*4+3]+"&deg;"+unitText;
		    	
		    	if(i!=5)
		    		desc=desc+";";
		    }
		    params.putString("description", desc);
		    params.putString("link", feed);
		    params.putString("picture", "http://www-scf.usc.edu/~csci571/2013Fall/hw8/weather.jpg");
		    params.putString("properties", "{\"Look at details \":{ text: 'here', href: \""+link+"\"}}");
	    }
	    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this,Session.getActiveSession(),params)).setOnCompleteListener(new OnCompleteListener() {
	        	
	            @Override
	            public void onComplete(Bundle values, FacebookException error) {
	            	//super.onComplete();
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(getBaseContext(),
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getBaseContext().getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getBaseContext().getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(getBaseContext().getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}	
	protected void fblogin(final int type) {
		Session.openActiveSession(this, true, new Session.StatusCallback() {

		    // callback when session changes state
		    
			@Override
		    public void call(Session session, SessionState state, Exception exception) {
		    			
		    	if (session.isOpened()) {
		    		// make request to the /me API
		    		//Request.executem
		    		Request.newMeRequest(session, new Request.GraphUserCallback(){

						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {
									//if(type==0)
										publishFeedDialog(type);	
				                }
							
						}}).executeAsync();
		    	}
		    }
		  });

		
	}

	public class Result{
		public String[] resultData;
		Bitmap bmp; 
		public Result(String[] responseResult) throws Exception{
			resultData = new String[39];
			if(resultData.length!=1){
			for(int i=0,j=0;i<responseResult.length;i++)
			{
				if(i!=6)
					resultData[j++]=responseResult[i];
				else
				{
					URL url = new URL(responseResult[i]);
					bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					//imageView.setImageBitmap(bmp);
				}
			}
			}
			else
			{
				resultData[0]="";
				bmp=null;
			}
		}
	}
	
	private class ThreadTask extends AsyncTask<String, Void, Result> {
		EditText x;
		ImageView img;
		TextView cityName,regionCountry,weatherText,weatherTemp,fR1Day,fR1Text,fR1High,fR1Low,forecastHeader;
		TextView fR0Day,fR0Text,fR0High,fR0Low,fR2Day,fR2Text,fR2High,fR2Low,fR3Day,fR3Text,fR3High,fR3Low;
		TextView fR4Day,fR4Text,fR4High,fR4Low,fR5Day,fR5Text,fR5High,fR5Low;
		
	    @Override
	    protected Result doInBackground(String... urls) {
	    	x= (EditText)findViewById(R.id.url_field);
	    	img=(ImageView)findViewById(R.id.imageWeather);
	    	cityName=(TextView)findViewById(R.id.cityName);
	    	regionCountry=(TextView)findViewById(R.id.regionCountry);
	    	weatherText=(TextView)findViewById(R.id.weatherText);
	    	weatherTemp=(TextView)findViewById(R.id.weatherTemp);
	    	
	    	fR0Day=(TextView)findViewById(R.id.fR0Day);
	    	fR0Text=(TextView)findViewById(R.id.fR0Text);
	    	fR0High=(TextView)findViewById(R.id.fR0High);
	    	fR0Low=(TextView)findViewById(R.id.fR0Low);
	    	
	    	fR1Day=(TextView)findViewById(R.id.fR1Day);
	    	fR1Text=(TextView)findViewById(R.id.fR1Text);
	    	fR1High=(TextView)findViewById(R.id.fR1High);
	    	fR1Low=(TextView)findViewById(R.id.fR1Low);
	    	
	    	fR2Day=(TextView)findViewById(R.id.fR2Day);
	    	fR2Text=(TextView)findViewById(R.id.fR2Text);
	    	fR2High=(TextView)findViewById(R.id.fR2High);
	    	fR2Low=(TextView)findViewById(R.id.fR2Low);
	    	
	    	fR3Day=(TextView)findViewById(R.id.fR3Day);
	    	fR3Text=(TextView)findViewById(R.id.fR3Text);
	    	fR3High=(TextView)findViewById(R.id.fR3High);
	    	fR3Low=(TextView)findViewById(R.id.fR3Low);
	    	
	    	fR4Day=(TextView)findViewById(R.id.fR4Day);
	    	fR4Text=(TextView)findViewById(R.id.fR4Text);
	    	fR4High=(TextView)findViewById(R.id.fR4High);
	    	fR4Low=(TextView)findViewById(R.id.fR4Low);
	    	
	    	fR5Day=(TextView)findViewById(R.id.fR5Day);
	    	fR5Text=(TextView)findViewById(R.id.fR5Text);
	    	fR5High=(TextView)findViewById(R.id.fR5High);
	    	fR5Low=(TextView)findViewById(R.id.fR5Low);
	    	
	    	
	    	forecastHeader=(TextView)findViewById(R.id.forecastHeader);
	      String urlString = urls[0];
	      String response[]=new String[40];
	      Result result=null;
	      
	      try {
	    	  URL url = new URL(urlString);
	    	  InputStream is = url.openStream();
	    	 // String jsonText = "{\"weather\":{\"location\":{\"region\":\"CA\",\"country\":\"USA\",\"city\":\"Pasedena\"},\"condition\":{\"text\":\"Fair\",\"temp\":\"85\"},\"forecast\":[{\"text\":\"Clear\",\"day\":\"Sat\",\"high\":\"92\",\"low\":\"61\"},{\"text\":\"Clear\",\"day\":\"Sat\",\"high\":\"92\",\"low\":\"61\"},{\"text\":\"Clear\",\"day\":\"Sat\",\"high\":\"92\",\"low\":\"61\"},{\"text\":\"Clear\",\"day\":\"Sat\",\"high\":\"92\",\"low\":\"61\"},{\"text\":\"Clear\",\"day\":\"Sat\",\"hight\":\"92\",\"low\":\"61\"}],\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Pasadena__CA/*http://weather.yahoo.com/forecast/USCA0840_f.html\",\"img\":\"http://l.yimg.com/a/i/us/we/52/33.gif\",\"feed\":\"http://weather.yahooapis.com/forecastrss?w=2468964&u=f\"}}";
	    	  		 ///"{\"weather\":{\"location\":{\"region\":\"CA\",\"country\":\"UnitedStates\",\"city\":\"Pasadena\"},\"condition\":{\"text\":\"Fair\",\"temp\":\"85\"},\"forecast\":[{\"text\":\"Clear\",\"day\":\"Sat\",\"high\":\"92\",\"low\":\"61\"},{\"text\":\"Sunny\",\"day\":\"Sun\",\"high\":\"95\",\"low\":\"60\"},{\"text\":\"PartlyCloudy\",\"day\":\"Mon\",\"high\":\"87\",\"low\":\"58\"},{\"text\":\"Sunny\",\"day\":\"Tue\",\"high\":\"78\",\"low\":\"57\"},{\"text\":\"FewShowers\",\"day\":\"Wed\",\"high\":\"68\",\"low\":\"53\"}],\"link\":\"http:\/\/us.rd.yahoo.com\/dailynews\/rss\/weather\/Pasadena__CA\/*http:\/\/weather.yahoo.com\/forecast\/USCA0840_f.html\",\"img\":\"http:\/\/l.yimg.com\/a\/i\/us\/we\/52\/33.gif\",\"feed\":\"http:\/\/weather.yahooapis.com\/forecastrss?w=2468964&u=f\"}}";
	    	  JSONObject json=null;
			
		    try {
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      if(jsonText!=""){
		    	  json = new JSONObject(jsonText);
		      //System.out.println(json.getJSONObject("location").getString("city"));
		      //response= json.toString();
		      //if(jsonText!=)
			      response[0]=json.getJSONObject("weather").getJSONObject("location").getString("city");
			      response[1]=json.getJSONObject("weather").getJSONObject("location").getString("region");
			      response[2]=json.getJSONObject("weather").getJSONObject("location").getString("country");
			      text=response[3]=json.getJSONObject("weather").getJSONObject("condition").getString("text");
			      response[4]=json.getJSONObject("weather").getString("link");
			      response[5]=json.getJSONObject("weather").getString("feed");
			      response[6]=json.getJSONObject("weather").getString("img");
			      JSONArray j=json.getJSONObject("weather").getJSONArray("forecast");
			      
			      for(int i=0;i<j.length();i++){
			    	  forecastData[i*4]=response[i*4+6+1]=j.getJSONObject(i).getString("text");
			    	  forecastData[i*4+1]=response[i*4+6+2]=j.getJSONObject(i).getString("day");
			    	  forecastData[i*4+2]=response[i*4+6+3]=j.getJSONObject(i).getString("high");
			    	  forecastData[i*4+3]=response[i*4+6+4]=j.getJSONObject(i).getString("low");
			      }
			      temp=response[27]=json.getJSONObject("weather").getJSONObject("condition").getString("temp");
			      unitText=response[28]=json.getJSONObject("weather").getJSONObject("units").getString("temperature");
			      //temp=response[28]
			      result=new Result(response);
			      
			      //Global Variables
			      feed=response[5];
			      link=response[4];
			      image=response[6];
			      
			      city=response[0];//+","+response[1]+","+response[2];
			      region="";
			      if(response[1]!="N/A"){region=region+", "+response[1];}
			      if(response[2]!="N/A"){region=region+", "+response[2];}
		      }
		      else
		      {
		    	  response[0]="";
		    	  result=new Result(response);
		    	  return result;
		      }
		    } 
		    catch (IOException e) {
		    	response[0]="";
		    	  result=new Result(response);
		    	  return result;
			} catch (JSONException e) {
				response[0]="";
		    	  result=new Result(response);
		    	  return result;
			} catch (Exception e) {
				response[0]="";
		    	  result=new Result(response);
		    	  return result;
			} finally {
		      //is.close();
		      
		          
		      //return response;
			      
		    }
		} catch (MalformedURLException e) {
			 
		} catch (IOException e1) {
			
		}
	      catch(Exception e){}
	      if(result==null){return null;}
	      
		return result;
	      
	    }

	    private String readAll(BufferedReader rd) throws IOException {
			// TODO Auto-generated method stub
	    	 StringBuilder sb = new StringBuilder();
	    	    int cp;
	    	    while ((cp = rd.read()) != -1) {
	    	      sb.append((char) cp);
	    	    }
	    	    return sb.toString();
		}

		@Override
	    protected void onPostExecute(Result result) {
			//textView.setText(result.);
			TableLayout weatherTable=(TableLayout)findViewById(R.id.weatherTable);
			LinearLayout fbForecastLayout=(LinearLayout)findViewById(R.id.ForecastFBLayout);
			LinearLayout fbWeatherLayout=(LinearLayout)findViewById(R.id.WeatherFBLayout);
			if(result!=null){
			if(!(result.resultData[0].isEmpty() || result.resultData[0]=="" || result.resultData[0]==null)){
				regionCountry.setVisibility(View.VISIBLE);//Text(result.resultData[1]+","+result.resultData[2]);
		    	weatherText.setVisibility(View.VISIBLE);
		    	weatherTemp.setVisibility(View.VISIBLE);
		    	img.setVisibility(View.VISIBLE);
		    	forecastHeader.setVisibility(View.VISIBLE);//(R.string.forecast);
				if(fbWeatherLayout.getVisibility()==View.INVISIBLE)
					fbWeatherLayout.setVisibility(View.VISIBLE);
				
				if(fbForecastLayout.getVisibility()==View.INVISIBLE)
					fbForecastLayout.setVisibility(View.VISIBLE);
				
				if(weatherTable.getVisibility()==View.INVISIBLE)
					weatherTable.setVisibility(View.VISIBLE);
				
		    	img.setImageBitmap(result.bmp);
		    	forecastHeader.setText(R.string.forecast);
		    	//x.setText(result.resultData[0]);
		    	cityName.setTextSize(30);
		    	cityName.setText(result.resultData[0]);
		    	regionCountry.setText(result.resultData[1]+","+result.resultData[2]);
		    	weatherText.setText(result.resultData[3]);
		    	weatherTemp.setText(result.resultData[26]+"\u00B0"+result.resultData[27]);
		    	//fR1Day,fR1Text,fR1High,fR1Low
		    	fR0Day.setText(R.string.Day);
		    	fR0Text.setText(R.string.Text);
		    	fR0High.setText(R.string.High);
		    	fR0Low.setText(R.string.Low);
		    	String degreeText="\u00B0"+result.resultData[27];
		    	fR1Day.setText(result.resultData[7]);
		    	fR1Text.setText(result.resultData[6]);
		    	fR1High.setText(result.resultData[8]+degreeText);
		    	fR1Low.setText(result.resultData[9]+degreeText);
	
		    	fR2Day.setText(result.resultData[11]);
		    	fR2Text.setText(result.resultData[10]);
		    	fR2High.setText(result.resultData[12]+degreeText);
		    	fR2Low.setText(result.resultData[13]+degreeText);
		    	
		    	fR3Day.setText(result.resultData[15]);
		    	fR3Text.setText(result.resultData[14]);
		    	fR3High.setText(result.resultData[16]+degreeText);
		    	fR3Low.setText(result.resultData[17]+degreeText);
		    	
		    	fR4Day.setText(result.resultData[19]);
		    	fR4Text.setText(result.resultData[18]);
		    	fR4High.setText(result.resultData[20]+degreeText);
		    	fR4Low.setText(result.resultData[21]+degreeText);
		    	
		    	fR5Day.setText(result.resultData[23]);
		    	fR5Text.setText(result.resultData[22]);
		    	fR5High.setText(result.resultData[24]+degreeText);
		    	fR5Low.setText(result.resultData[25]+degreeText);
			}
			
			else
			{
				cityName.setTextSize(15);
				cityName.setText("Weather Information cannot be Found");
				//if(fbWeatherLayout.getVisibility()==View.INVISIBLE)
					fbWeatherLayout.setVisibility(View.INVISIBLE);
				
				//if(fbForecastLayout.getVisibility()==View.INVISIBLE)
					fbForecastLayout.setVisibility(View.INVISIBLE);
				
				//if(weatherTable.getVisibility()==View.INVISIBLE)
					weatherTable.setVisibility(View.INVISIBLE);
					regionCountry.setVisibility(View.INVISIBLE);//Text(result.resultData[1]+","+result.resultData[2]);
			    	weatherText.setVisibility(View.INVISIBLE);
			    	weatherTemp.setVisibility(View.INVISIBLE);
			    	img.setVisibility(View.INVISIBLE);
			    	forecastHeader.setVisibility(View.INVISIBLE);
			}
		  }
			else{
				cityName.setTextSize(15);
				cityName.setText("Weather Information cannot be Found");
				//if(fbWeatherLayout.getVisibility()==View.INVISIBLE)
					fbWeatherLayout.setVisibility(View.INVISIBLE);
				
				//if(fbForecastLayout.getVisibility()==View.INVISIBLE)
					fbForecastLayout.setVisibility(View.INVISIBLE);
				
				//if(weatherTable.getVisibility()==View.INVISIBLE)
					weatherTable.setVisibility(View.INVISIBLE);
					regionCountry.setVisibility(View.INVISIBLE);//Text(result.resultData[1]+","+result.resultData[2]);
			    	weatherText.setVisibility(View.INVISIBLE);
			    	weatherTemp.setVisibility(View.INVISIBLE);
			    	img.setVisibility(View.INVISIBLE);
			    	forecastHeader.setVisibility(View.INVISIBLE);
			}
	    }
	  }


	private int validate(String city) {
		// TODO Auto-generated method stub
  	  	if(city==null || city=="" || city.length()==0 || city.isEmpty())
  	  	{
  	  		CharSequence text = "Enter City Name";
  	  		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
  	  		return 0;
  	  	}
  	  	
  	  	if(city.matches(".\\d+."))
  	  	{
  	  		if(city.length()==5){
	  			return 1;
  	  		}
  	  		CharSequence text = "ZipCode should be 5 digit length\n Example : 90089";
  	  		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	  		return 0;
  	  	}
  	  	
  	  	else if(city.matches("[a-zA-Z0-9~.,'@#\t ]+,.\\w+")||city.matches("[a-zA-Z0-9~.,'@#\t ]+,.\\w+,.\\w+")){
		  		//Toast.makeText(this, city, Toast.LENGTH_LONG).show();
		  		return 2;
  	  	}
  	  	
  	  	else{
  	  		CharSequence text = "Invalid location: must include state or country seperated by comma\n Example: Los Angeles,CA";
	  		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	  		return 0;
  	  	}
  	  	//return 0;
		
	}
		

}
