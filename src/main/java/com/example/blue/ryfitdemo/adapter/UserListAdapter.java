package com.example.blue.ryfitdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.blue.R;
import com.example.blue.ryfitdemo.entity.UserData;

import java.util.List;
import java.util.Map;

/**
 * @author Mars E_mail:HouJianChao1204@163
 * @version CareteTime:2014-3-13 下午3:38:46
 * @description Class description
 */
public class UserListAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, String>>  userData;
	public UserListAdapter(Context context,List<Map<String, String>>  userData){
		this.context=context;
		this.userData=userData;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return userData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			holder=new ViewHolder();
			view =LayoutInflater.from(context).inflate(R.layout.tizhi_user_list_item, null);
			holder.user_id=(TextView) view.findViewById(R.id.user_id);
			holder.age=(TextView) view.findViewById(R.id.age);
			holder.height=(TextView) view.findViewById(R.id.height);
			holder.sex=(TextView) view.findViewById(R.id.sex);
			holder.word_sex=(TextView) view.findViewById(R.id.word_sex);
			holder.ll_index_tow=(LinearLayout) view.findViewById(R.id.ll_index_tow);
			view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		Map<String, String> map=userData.get(position);
		if(map.get(UserData.INDEX)==null){
			holder.user_id.setText((Integer.parseInt(map.get(UserData.NUMERICAL_ORDER), 16))+"");
			holder.ll_index_tow.setVisibility(View.INVISIBLE);
			holder.word_sex.setText("Idle");
			holder.sex.setVisibility(View.GONE);
		}else{
			holder.ll_index_tow.setVisibility(View.VISIBLE);
			holder.word_sex.setText(R.string.sex);
			holder.sex.setVisibility(View.VISIBLE);
		holder.user_id.setText(map.get(UserData.NUMERICAL_ORDER)+"");
		holder.age.setText(map.get(UserData.AGE));
		holder.height.setText(map.get(UserData.HEIGHT)+"cm");
		holder.sex.setText((Integer.parseInt(map.get(UserData.SEX)))==0?"Woman":"Man");
		}
		return view;
	}

	class ViewHolder{
		TextView user_id;
		TextView age;
		TextView height;
		TextView sex;
		LinearLayout ll_index_tow;
		TextView word_sex;
	}
}
