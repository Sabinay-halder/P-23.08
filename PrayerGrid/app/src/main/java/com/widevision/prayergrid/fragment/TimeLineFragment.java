package com.widevision.prayergrid.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.HomeActivity;
import com.widevision.prayergrid.dao.AnswerdPrayerListDao;
import com.widevision.prayergrid.dao.DeletePrayerDao;
import com.widevision.prayergrid.dao.GetPrayerListDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.ReportinappropriateDao;
import com.widevision.prayergrid.dao.SetPrayerDao;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.DBHelper;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;
import com.widevision.prayergrid.util.ValidationTemplate;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimeLineFragment extends Fragment {

    @Bind(R.id.country)
    Spinner mCountrySpinner;
    @Bind(R.id.category)
    Spinner mCategorySpinner;
    @Bind(R.id.prayer_request_layout)
    LinearLayout mPrayerRequestLayout;
    @Bind(R.id.answered_prayer_layout)
    LinearLayout mAnsweredPrayerLayout;
    @Bind(R.id.timeline_list)
    ListView mList;
    CustomAdapterAnswerPrayer adapterTimeLineAnswerd;
    private ArrayList<GsonClass.Post_info> answerdList = new ArrayList<>();

    private String[] categoryList;

    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    private RefreshReceiver refreshReceiver;

    public static int itemClickTag = 0;
    private int a = 0, b = 0, enoughTag = 0, pageCount = 0;
    @Bind(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private String cat = "", country = "";
    private final ArrayList<GsonClass.Post_info> dataList = new ArrayList<>();
    private CustomAdapterTimeLine adapterTimeLine;
    private CustomAdapterAnswerPrayer adapterAnswerPrayer;

    public TimeLineFragment() {
        // Required empty public constructor
    }

    public static TimeLineFragment newInstance() {
        return new TimeLineFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_line, container, false);
        ButterKnife.bind(this, view);
        addCategory();
        init();
        a = 0;
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCountrySpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.row_spinner, HomeActivity.countries) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#3C4D5C"));
                return view;
            }
        });

        mCategorySpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.row_spinner, categoryList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#3C4D5C"));
                text.setSingleLine(false);
                return view;
            }
        });

        refreshReceiver = new RefreshReceiver();
        getActivity().registerReceiver(refreshReceiver, new IntentFilter("timeline"));

        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  Constant.setAlert(getActivity(), "Not implemented yet.");
                if (b != 0) {
                    pageCount = 0;
                    if (i != 0) {
                        country = DBHelper.countryBeanArrayList.get(i).getCountryCode();
                        getTimeLine(cat, "0", DBHelper.countryBeanArrayList.get(i).getCountryCode());
                    } else {
                        country = "";
                        getTimeLine(cat, "" + pageCount, country);
                    }
                }
                b = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (a != 0) {
                    pageCount = 0;
                    if (i != 0) {
                        getTimeLine("" + (i - 1), "" + pageCount, country);
                        cat = "" + (i - 1);
                    } else {
                        cat = "";
                        getTimeLine("", "" + pageCount, country);
                    }
                }
                a = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mPrayerRequestLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    if (itemClickTag != 1) {
                        mAnsweredPrayerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        mPrayerRequestLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        fragmentTransaction.add(R.id.frame, PrayerpostFragment.newInstance(Constant._type));//type 0 for user and 1 for church
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        itemClickTag = 1;
                    }
                }
            }
        });

        mAnsweredPrayerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    if (itemClickTag != 2) {
                        pageCount = 0;
                        mAnsweredPrayerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        mPrayerRequestLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        getAnswerdPrayerList();
                        itemClickTag = 2;
                    }
                }
            }
        });

        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (itemClickTag == 0) {
                    pageCount = 0;
                    getTimeLine(cat, "" + pageCount, country);
                } else if (itemClickTag == 2) {
                    pageCount = 0;
                    getAnswerdPrayerList();
                }
            }
        });

        pageCount = 0;
        cat = "";
        country = "";
        getTimeLine("", "" + pageCount, country);
    }

    private void init() {
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        progressLoaderHelper.dismissProgress();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void getTimeLine(String category, final String count, final String country) {
        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(getActivity());
            GetPrayerListDao prayerListDao = new GetPrayerListDao(Constant._id, category, count, country);
            prayerListDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    swipeRefreshLayout.setRefreshing(false);
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                mList.setVisibility(View.VISIBLE);
                                if (pageCount == 0) {
                                    enoughTag = 0;
                                    dataList.clear();
                                    dataList.addAll(result.post_info);
                                    if (adapterTimeLine != null) {
                                        adapterTimeLine.notifyDataSetChanged();
                                    } else {
                                        adapterTimeLine = new CustomAdapterTimeLine(getActivity(), dataList);
                                        mList.setAdapter(adapterTimeLine);
                                    }
                                } else {
                                    dataList.addAll(result.post_info);
                                    if (adapterTimeLine != null) {
                                        adapterTimeLine.notifyDataSetChanged();
                                    } else {
                                        adapterTimeLine = new CustomAdapterTimeLine(getActivity(), dataList);
                                        mList.setAdapter(adapterTimeLine);
                                    }
                                }
                            } else {
                                enoughTag = 1;
                                if (pageCount == 0) {
                                    mList.setVisibility(View.GONE);
                                    Constant.setAlert(getActivity(), "No prayer to show.");
                                }
                            }
                        } else {
                            enoughTag = 1;
                            if (pageCount == 0) {
                                mList.setVisibility(View.GONE);
                                Constant.setAlert(getActivity(), "No prayer to show.");
                            }
                        }
                    } else {
                        enoughTag = 1;
                        //  mList.setVisibility(View.GONE);
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                    }
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.no_internet));
        }
    }

    private void getAnswerdPrayerList() {
        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(getActivity());
            AnswerdPrayerListDao prayerListDao = new AnswerdPrayerListDao(Constant._id, "" + pageCount);
            prayerListDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    swipeRefreshLayout.setRefreshing(false);
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            enoughTag = 0;
                            if (result.post_info != null && result.post_info.size() != 0) {
                                mList.setVisibility(View.VISIBLE);
                                if (pageCount == 0) {
                                    enoughTag = 0;
                                    answerdList.clear();
                                    answerdList.addAll(result.post_info);
                                    if (adapterAnswerPrayer != null) {
                                        adapterAnswerPrayer.notifyDataSetChanged();
                                    } else {
                                        adapterAnswerPrayer = new CustomAdapterAnswerPrayer(getActivity(), answerdList);
                                        mList.setAdapter(adapterAnswerPrayer);
                                    }
                                } else {
                                    answerdList.addAll(result.post_info);
                                    if (adapterAnswerPrayer != null) {
                                        adapterAnswerPrayer.notifyDataSetChanged();
                                    } else {
                                        adapterAnswerPrayer = new CustomAdapterAnswerPrayer(getActivity(), answerdList);
                                        mList.setAdapter(adapterAnswerPrayer);
                                    }
                                }
                            } else {
                                enoughTag = 1;
                                Constant.setAlert(getActivity(), "No prayer to show.");
                            }
                        } else {
                            enoughTag = 1;
                            Constant.setAlert(getActivity(), "No prayer to show.");
                        }
                    } else {
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                    }
                }
            });
        } else {
            Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.no_internet));
        }
    }

    class CustomAdapterTimeLine extends BaseAdapter {

        private final LayoutInflater layoutInflater;
        private ViewHolder viewHolder;
        private final ArrayList<GsonClass.Post_info> list;
        private final AQuery aQuery;

        public CustomAdapterTimeLine(Context context, ArrayList<GsonClass.Post_info> list) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.list = list;
            aQuery = new AQuery(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void removeItem(GsonClass.Post_info item) {
            list.remove(item);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final GsonClass.Post_info item = (GsonClass.Post_info) getItem(i);
            if (view == null) {
                view = layoutInflater.inflate(R.layout.timeline_items, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.commentsTxt = (LinearLayout) view.findViewById(R.id.comments);
                viewHolder.messagesTxt = (LinearLayout) view.findViewById(R.id.messages);
                viewHolder.prayersTxt = (LinearLayout) view.findViewById(R.id.prayers);
                viewHolder.prayTxtLayout = (LinearLayout) view.findViewById(R.id.pray);
                viewHolder.userName = (TextView) view.findViewById(R.id.username);
                viewHolder.postTxt = (TextView) view.findViewById(R.id.post);
                viewHolder.countTxt = (TextView) view.findViewById(R.id.count);
                viewHolder.prayTxt = (TextView) view.findViewById(R.id.pray_txt);
                viewHolder.categoryTxt = (TextView) view.findViewById(R.id.category);
                viewHolder.profilePic = (ImageView) view.findViewById(R.id.profile);
                viewHolder.prayImg = (ImageView) view.findViewById(R.id.pray_image);
                viewHolder.arrowImg = (ImageView) view.findViewById(R.id.arrow);
                viewHolder.post_time = (TextView) view.findViewById(R.id.post_time);
                viewHolder.name_txt = (TextView) view.findViewById(R.id.name_txt);
                viewHolder.post_time.setVisibility(View.VISIBLE);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (i == getCount() - 1) {
                if (enoughTag == 0) {
                    pageCount = pageCount + 1;
                    getTimeLine(cat, "" + pageCount, country);
                }
            }
            viewHolder.postTxt.setText(item.message);
            viewHolder.countTxt.setText("(" + item.count + ")");
            viewHolder.categoryTxt.setText(item.category);

            String date = item.created_at;
            String[] date_data = date.split(" ");
            viewHolder.post_time.setText(date_data[0]);

            if (item.type.equals("2")) {
                viewHolder.userName.setText("Anonymous");
                viewHolder.profilePic.setVisibility(View.GONE);
                viewHolder.name_txt.setText(item.name.substring(0, 1).toUpperCase());
                viewHolder.name_txt.setVisibility(View.VISIBLE);
            } else {
                if (item.profilepic.equals("http://prayergrid.org/uploads/profilepic/default.png")) {
                    // viewHolder.profilePic.setImageBitmap(getBitmapFromText(100, 100, item.name.substring(0, 1).toUpperCase()));
                    viewHolder.profilePic.setVisibility(View.GONE);
                    viewHolder.name_txt.setText(item.name.substring(0, 1).toUpperCase());
                    viewHolder.name_txt.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.name_txt.setVisibility(View.GONE);
                    viewHolder.profilePic.setVisibility(View.VISIBLE);
                    aQuery.id(viewHolder.profilePic).image(item.profilepic, true, true, 100, R.drawable.default_pic);
                }

                viewHolder.userName.setText(item.name);
            }

            if (item.prayer.equals("Yes")) {
                viewHolder.prayTxt.setText("Prayed");
                viewHolder.prayImg.setImageResource(R.drawable.pray_icon);
            } else {
                viewHolder.prayTxt.setText("Pray");
                viewHolder.prayImg.setImageResource(R.drawable.pray);
            }
            if (item.userid.equals(Constant._id)) {
                viewHolder.messagesTxt.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.messagesTxt.setVisibility(View.VISIBLE);
            }
            viewHolder.commentsTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        fragmentTransaction.add(R.id.frame, ViewCommentsFragment.newInstance(item.pid));
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            });

            viewHolder.prayersTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        if (!item.count.equals("0")) {
                            FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            fragmentTransaction.add(R.id.frame, PrayerarListFragment.newInstance(item.pid));
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                }
            });

            viewHolder.messagesTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        PostMessageDialogFragment fragment = PostMessageDialogFragment.newInstance(item.userid);
                        fragment.show(HomeActivity.fragmentManager, "Send Message");
                    }
                }
            });

            viewHolder.prayTxtLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        if (item.prayer.equals("No")) {
                            setPrayers(item.pid);
                            int c = Integer.parseInt(item.count) + 1;
                            item.count = "" + c;
                            item.prayer = "Yes";
                            notifyDataSetChanged();
                        }
                    }
                }
            });
            viewHolder.arrowImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        showPopup(view, item);
                    }
                }
            });
            return view;
        }

        private Bitmap getBitmapFromText(int height, int width, String text) {

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            Random rnd = new Random();
            paint.setARGB(255, 251, 108, 78);
            //   paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            //paint.setShadowLayer(10.0f, 0.0f, 2.0f, 0xFF000000);
            canvas.drawPaint(paint);
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setTextSize(55.f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, (width / 2.f), (height / 2.f) + (height / 5.f), paint);
            return bitmap;
        }


        void showPopup(View view, final GsonClass.Post_info item) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View v = layoutInflater.inflate(R.layout.popup_arrow_layout, null);
            final PopupWindow popupWindow = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView report = (TextView) v.findViewById(R.id.report);
            TextView delete = (TextView) v.findViewById(R.id.delete);
            if (!item.userid.equals(Constant._id)) {
                delete.setVisibility(View.GONE);
            }
            report.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              if (Constant.buttonEnable) {
                                                  Constant.setButtonEnable();
                                                  popupWindow.dismiss();
                                                  if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                                                      ReportinappropriateDao reportinappropriateDao = new ReportinappropriateDao(Constant._id, "0", item.pid);
                                                      reportinappropriateDao.query(new AsyncCallback<GsonClass>() {
                                                          @Override
                                                          public void onOperationCompleted(GsonClass result, Exception e) {

                                                          }
                                                      });
                                                      Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.report_msg));
                                                  } else {
                                                      Constant.setAlert(getActivity(), getString(R.string.no_internet));
                                                  }
                                              }
                                          }
                                      }
            );

            delete.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View view) {
                                              if (Constant.buttonEnable) {
                                                  Constant.setButtonEnable();
                                                  popupWindow.dismiss();
                                                  if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                                                      DeletePrayerDao deletePrayerDao = new DeletePrayerDao(Constant._id, item.pid);
                                                      deletePrayerDao.query(new AsyncCallback<GsonClass>() {
                                                          @Override
                                                          public void onOperationCompleted(GsonClass result, Exception e) {
                                                              if (e == null && result != null) {
                                                                  if (result.success.equals("1")) {
                                                                      adapterTimeLine.removeItem(item);
                                                                      Toast.makeText(getActivity(), "Prayer deleted successfully.", Toast.LENGTH_SHORT).show();
                                                                  } else {
                                                                      Constant.setAlert(getActivity(), result.message);
                                                                  }
                                                              } else {
                                                                  Constant.setAlert(getActivity(), getString(R.string.wrong));
                                                              }
                                                          }
                                                      });
                                                  } else {
                                                      Constant.setAlert(getActivity(), getString(R.string.no_internet));
                                                  }
                                                  // Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.report_msg));
                                              }
                                          }
                                      }

            );
            popupWindow.showAsDropDown(view, 0, -15);
        }

        class ViewHolder {
            ImageView profilePic, prayImg, arrowImg;
            TextView userName, categoryTxt, postTxt, countTxt, prayTxt, post_time, name_txt;
            LinearLayout messagesTxt, commentsTxt, prayersTxt, prayTxtLayout;
        }
    }

    class CustomAdapterAnswerPrayer extends BaseAdapter {

        private final LayoutInflater layoutInflater;
        private ViewHolder viewHolder;
        private final ArrayList<GsonClass.Post_info> list;
        private final AQuery aQuery;

        public CustomAdapterAnswerPrayer(Context context, ArrayList<GsonClass.Post_info> list) {
            layoutInflater = getLayoutInflater(null);
            this.list = list;
            aQuery = new AQuery(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void removeItem(GsonClass.Post_info item) {
            list.remove(item);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final GsonClass.Post_info item = (GsonClass.Post_info) getItem(i);
            if (view == null) {
                view = layoutInflater.inflate(R.layout.timeline_items, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.commentsTxt = (LinearLayout) view.findViewById(R.id.comments);
                viewHolder.messagesTxt = (LinearLayout) view.findViewById(R.id.messages);
                viewHolder.prayersTxt = (LinearLayout) view.findViewById(R.id.prayers);
                viewHolder.prayTxtLayout = (LinearLayout) view.findViewById(R.id.pray);
                viewHolder.userName = (TextView) view.findViewById(R.id.username);
                viewHolder.postTxt = (TextView) view.findViewById(R.id.post);
                viewHolder.countTxt = (TextView) view.findViewById(R.id.count);
                viewHolder.categoryTxt = (TextView) view.findViewById(R.id.category);
                viewHolder.profilePic = (ImageView) view.findViewById(R.id.profile);
                viewHolder.arrowImg = (ImageView) view.findViewById(R.id.arrow);
                viewHolder.name_txt = (TextView) view.findViewById(R.id.name_txt);
                viewHolder.prayTxtLayout.setVisibility(View.INVISIBLE);
                viewHolder.prayersTxt.setVisibility(View.GONE);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            if (i == getCount() - 1) {
                if (enoughTag == 0) {
                    pageCount = pageCount + 1;
                    getAnswerdPrayerList();
                }
            }

            viewHolder.postTxt.setText(item.message);
            viewHolder.countTxt.setText("(" + item.count + ")");
            viewHolder.categoryTxt.setText(item.category);

            if (item.type.equals("2")) {
                viewHolder.userName.setText("Anonymous");
                viewHolder.profilePic.setVisibility(View.GONE);
                viewHolder.name_txt.setText(item.name.substring(0, 1).toUpperCase());
                viewHolder.name_txt.setVisibility(View.VISIBLE);
            } else {
                if (item.profilepic.equals("http://prayergrid.org/uploads/profilepic/default.png")) {
                    // viewHolder.profilePic.setImageBitmap(getBitmapFromText(100, 100, item.name.substring(0, 1).toUpperCase()));
                    viewHolder.profilePic.setVisibility(View.GONE);
                    viewHolder.name_txt.setText(item.name.substring(0, 1).toUpperCase());
                    viewHolder.name_txt.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.name_txt.setVisibility(View.GONE);
                    viewHolder.profilePic.setVisibility(View.VISIBLE);
                    aQuery.id(viewHolder.profilePic).image(item.profilepic, true, true, 100, R.drawable.default_pic);
                }

                viewHolder.userName.setText(item.name);
            }

            if (item.userid.equals(Constant._id)) {
                viewHolder.messagesTxt.setVisibility(View.GONE);
            } else {
                viewHolder.messagesTxt.setVisibility(View.VISIBLE);
            }

            viewHolder.commentsTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.frame, ViewCommentsFragment.newInstance(item.pid));
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            });

            viewHolder.prayersTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        if (!item.count.equals("0")) {
                            FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.frame, PrayerarListFragment.newInstance(item.pid));
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                }
            });

            viewHolder.messagesTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        PostMessageDialogFragment fragment = PostMessageDialogFragment.newInstance(item.userid);
                        fragment.show(HomeActivity.fragmentManager, "Send Message");
                    }
                }
            });

            viewHolder.prayTxtLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        if (item.prayer.equals("No")) {
                            setPrayers(item.pid);
                            int c = Integer.parseInt(item.count) + 1;
                            item.count = "" + c;
                            item.prayer = "Yes";
                            notifyDataSetChanged();
                        }
                    }
                }
            });

            viewHolder.arrowImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        showPopup(view, item);
                    }
                }
            });
            return view;
        }

        void showPopup(View view, final GsonClass.Post_info item) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View v = layoutInflater.inflate(R.layout.popup_arrow_layout, null);
            final PopupWindow popupWindow = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView report = (TextView) v.findViewById(R.id.report);
            TextView delete = (TextView) v.findViewById(R.id.delete);
            if (!item.userid.trim().equals(Constant._id)) {
                delete.setVisibility(View.GONE);
            }
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        popupWindow.dismiss();
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.report_msg));
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        popupWindow.dismiss();
                        if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                            DeletePrayerDao deletePrayerDao = new DeletePrayerDao(Constant._id, item.pid);
                            deletePrayerDao.query(new AsyncCallback<GsonClass>() {
                                @Override
                                public void onOperationCompleted(GsonClass result, Exception e) {
                                    if (e == null && result != null) {
                                        if (result.success.equals("1")) {
                                            adapterAnswerPrayer.removeItem(item);
                                            Toast.makeText(getActivity(), "Prayer deleted successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Constant.setAlert(getActivity(), result.message);
                                        }
                                    } else {
                                        Constant.setAlert(getActivity(), getString(R.string.wrong));
                                    }
                                }
                            });
                        } else {
                            Constant.setAlert(getActivity(), getString(R.string.no_internet));
                        }
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.report_msg));
                    }
                }
            });
            popupWindow.showAsDropDown(view, 0, -15);
        }

        class ViewHolder {
            ImageView profilePic, arrowImg;
            TextView userName, categoryTxt, postTxt, countTxt, name_txt;
            LinearLayout messagesTxt, commentsTxt, prayersTxt, prayTxtLayout;
        }
    }

    private void setPrayers(String prayer_id) {
        SetPrayerDao prayerDao = new SetPrayerDao(Constant._id, prayer_id, Constant._type);
        prayerDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                if (e == null && result != null) {
                    if (!result.success.equals("1")) {
                        Constant.setAlert(getActivity(), result.message);
                    }
                } else {
                    Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                }
            }
        });
    }

    private void addCategory() {
        categoryList = getActivity().getResources().getStringArray(R.array.category);
    }

    class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            pageCount = 0;
            getTimeLine(cat, "" + pageCount, country);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(refreshReceiver);
        progressLoaderHelper.dismissProgress();
    }
}