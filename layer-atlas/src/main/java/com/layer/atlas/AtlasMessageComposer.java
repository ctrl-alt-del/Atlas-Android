/*
 * Copyright (c) 2015 Layer. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.layer.atlas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.layer.atlas.messagetypes.AttachmentSender;
import com.layer.atlas.messagetypes.MessageSender;
import com.layer.atlas.messagetypes.text.TextSender;
import com.layer.atlas.tenor.SmartGifsUtils;
import com.layer.atlas.tenor.adapters.GifAdapter;
import com.layer.atlas.tenor.adapters.OnDismissPopupWindowListener;
import com.layer.atlas.tenor.presenters.impl.GifPresenter;
import com.layer.atlas.tenor.rvitem.ResultRVItem;
import com.layer.atlas.tenor.threepartgif.GifSender;
import com.layer.atlas.tenor.views.IKeyboardView;
import com.layer.atlas.util.EditTextUtil;
import com.layer.sdk.LayerClient;
import com.layer.sdk.listeners.LayerTypingIndicatorListener;
import com.layer.sdk.messaging.Conversation;
import com.tenor.android.sdk.constants.StringConstant;
import com.tenor.android.sdk.listeners.TextWatcherAdapter;
import com.tenor.android.sdk.models.Result;
import com.tenor.android.sdk.responses.BaseError;
import com.tenor.android.sdk.responses.GifsResponse;
import com.tenor.android.sdk.rvwidgets.AbstractRVItem;
import com.tenor.android.sdk.rvwidgets.EndlessRVOnScrollListener;
import com.tenor.android.sdk.utils.AbstractListUtils;
import com.tenor.android.sdk.utils.AbstractLocaleUtils;
import com.tenor.android.sdk.utils.AbstractViewUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class AtlasMessageComposer extends FrameLayout implements IKeyboardView {
    private EditText mMessageEditText;
    private Button mSendButton;
    private ImageView mAttachButton;
    private ImageView mOpenGifsRVButton;

    private LayerClient mLayerClient;
    private Conversation mConversation;

    private TextSender mTextSender;
    private GifSender mGifSender;
    private ArrayList<AttachmentSender> mAttachmentSenders = new ArrayList<AttachmentSender>();
    private MessageSender.Callback mMessageSenderCallback;

    private PopupWindow mAttachmentMenu;
    private RecyclerView mGifsRV;

    // styles
    private boolean mEnabled;
    private int mTextColor;
    private float mTextSize;
    private Typeface mTypeFace;
    private int mTextStyle;
    private int mUnderlineColor;
    private int mCursorColor;
    private Drawable mAttachmentSendersBackground;
    private GifPresenter mPresenter;
    private String mNextPageId = "";
    private GifAdapter<Context> mAdapter;
    private static Call<GifsResponse> sSearchGifsCall;
    private static boolean sTextChanged;


    public AtlasMessageComposer(Context context) {
        super(context);
        initAttachmentMenu(context, null, 0);
    }

    public AtlasMessageComposer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AtlasMessageComposer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseStyle(context, attrs, defStyle);
        initAttachmentMenu(context, attrs, defStyle);
    }

    private void showGifSearchView() {
        AbstractViewUtils.showView(mGifsRV);
        mMessageEditText.setHint(R.string.tenor_message_composer_gif_search_hint);
        mOpenGifsRVButton.setImageResource(R.drawable.ic_arrow_back_white_24dp_tinted);
    }

    private void hideGifSearchView() {
        AbstractViewUtils.hideView(mGifsRV);
        mMessageEditText.setHint(R.string.atlas_message_composer_hint);
        mOpenGifsRVButton.setImageResource(R.drawable.ic_tenor_logo_tinted);
    }

    private void performGifSearch(Editable s) {

        String str = s.toString().trim();
        if (str.length() == 0) {
            searchSmartOrTrendingGifs();
            return;
        }

        if (mPresenter == null) {
            return;
        }

        final boolean isAppend = !sTextChanged && !SmartGifsUtils.isMessageChanged();
        if (!isAppend) {
            mNextPageId = StringConstant.EMPTY;
        }

        if (sSearchGifsCall != null) {
            sSearchGifsCall.cancel();
        }
        sSearchGifsCall = mPresenter.search(str, AbstractLocaleUtils.getCurrentLocaleName(getContext()),
                24, mNextPageId, null, isAppend);
    }

    /**
     * Prepares this AtlasMessageComposer for use.
     *
     * @return this AtlasMessageComposer.
     */
    public AtlasMessageComposer init(LayerClient layerClient) {
        LayoutInflater.from(getContext()).inflate(R.layout.tenor_message_composer, this);

        mLayerClient = layerClient;

        mGifsRV = (RecyclerView) findViewById(R.id.tmc_rv_gifs);
        mGifsRV.setFocusable(true);

        mPresenter = new GifPresenter(this);
        mAdapter = new GifAdapter<>(getContext()).setDismissPopupWindowListener(new OnDismissPopupWindowListener() {
            @Override
            public void dismiss() {
                hideGifSearchView();
            }
        });

        mGifsRV.setAdapter(mAdapter);

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mGifsRV.setLayoutManager(layoutManager);

        mGifsRV.addOnScrollListener(new EndlessRVOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (mMessageEditText.getText().toString().trim().length() > 0) {
                    performGifSearch(mMessageEditText.getText());
                } else {
                    searchSmartOrTrendingGifs();
                }
            }
        });

        mOpenGifsRVButton = (ImageView) findViewById(R.id.tmc_iv_open_gifs_rv);
        mOpenGifsRVButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGifsRV.getVisibility() == VISIBLE) {
                    hideGifSearchView();
                    return;
                }

                showGifSearchView();
                if (mMessageEditText.getText().toString().trim().length() > 0) {
                    performGifSearch(mMessageEditText.getText());
                } else {
                    searchSmartOrTrendingGifs();
                }
            }
        });

        mAttachButton = (ImageView) findViewById(R.id.attachment);
        mAttachButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LinearLayout menu = (LinearLayout) mAttachmentMenu.getContentView();
                menu.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mAttachmentMenu.showAsDropDown(v, 0, -menu.getMeasuredHeight() - v.getHeight());
            }
        });

        mMessageEditText = (EditText) findViewById(R.id.message_edit_text);
        mMessageEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                sTextChanged = true;
                if (mConversation == null || mConversation.isDeleted()) return;
                if (s.length() > 0) {
                    mSendButton.setEnabled(isEnabled());
                    mConversation.send(LayerTypingIndicatorListener.TypingIndicator.STARTED);
                } else {
                    mSendButton.setEnabled(false);
                    mConversation.send(LayerTypingIndicatorListener.TypingIndicator.FINISHED);
                }

                if (mGifsRV.getVisibility() == View.VISIBLE) {
                    performGifSearch(s);
                }
            }
        });

        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!mTextSender.requestSend(mMessageEditText.getText().toString())) return;
                mMessageEditText.setText("");
                mSendButton.setEnabled(false);
            }
        });
        applyStyle();
        return this;
    }

    /**
     * Sets the Conversation used for sending Messages.
     *
     * @param conversation the Conversation used for sending Messages.
     * @return This AtlasMessageComposer.
     */
    public AtlasMessageComposer setConversation(Conversation conversation) {
        mConversation = conversation;
        if (mTextSender != null) mTextSender.setConversation(conversation);
        if (mGifSender != null) mGifSender.setConversation(conversation);
        for (AttachmentSender sender : mAttachmentSenders) {
            sender.setConversation(conversation);
        }
        return this;
    }

    /**
     * Sets a listener for receiving the message EditText focus change callbacks.
     *
     * @param listener Listener for receiving the message EditText focus change callbacks.
     * @return This AtlasMessageComposer.
     */
    public AtlasMessageComposer setOnMessageEditTextFocusChangeListener(OnFocusChangeListener listener) {
        mMessageEditText.setOnFocusChangeListener(listener);
        return this;
    }

    /**
     * Sets the TextSender used for sending composed text messages.
     *
     * @param textSender TextSender used for sending composed text messages.
     * @return This AtlasMessageComposer.
     */
    public AtlasMessageComposer setTextSender(TextSender textSender) {
        mTextSender = textSender;
        mTextSender.init(this.getContext().getApplicationContext(), mLayerClient);
        mTextSender.setConversation(mConversation);
        if (mMessageSenderCallback != null) mTextSender.setCallback(mMessageSenderCallback);
        return this;
    }

    /**
     * Sets the GifSender used for sending composed gif messages.
     *
     * @param gifSender GifSender used for sending composed gif messages.
     * @return This AtlasMessageComposer.
     */
    public AtlasMessageComposer setGifSender(GifSender gifSender) {
        mGifSender = gifSender;
        mGifSender.init(this.getContext().getApplicationContext(), mLayerClient);
        mGifSender.setConversation(mConversation);
        if (mMessageSenderCallback != null) mGifSender.setCallback(mMessageSenderCallback);
        mAdapter.setGifSender(mGifSender);
        return this;
    }

    /**
     * Adds AttachmentSenders to this AtlasMessageComposer's attachment menu.
     *
     * @param senders AttachmentSenders to add to this AtlasMessageComposer's attachment menu.
     * @return This AtlasMessageComposer.
     */
    public AtlasMessageComposer addAttachmentSenders(AttachmentSender... senders) {
        for (AttachmentSender sender : senders) {
            if (sender.getTitle() == null && sender.getIcon() == null) {
                throw new NullPointerException("Attachment handlers must have at least a title or icon specified.");
            }
            sender.init(this.getContext().getApplicationContext(), mLayerClient);
            sender.setConversation(mConversation);
            if (mMessageSenderCallback != null) sender.setCallback(mMessageSenderCallback);
            mAttachmentSenders.add(sender);
            addAttachmentMenuItem(sender);
        }
        if (!mAttachmentSenders.isEmpty()) mAttachButton.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Sets an optional callback for receiving MessageSender events.  If non-null, overrides any
     * callbacks already set on MessageSenders.
     *
     * @param callback Callback to receive MessageSender events.
     * @return This AtlasMessageComposer.
     */
    public AtlasMessageComposer setMessageSenderCallback(MessageSender.Callback callback) {
        mMessageSenderCallback = callback;
        if (mMessageSenderCallback == null) return this;
        if (mTextSender != null) mTextSender.setCallback(callback);
        for (AttachmentSender sender : mAttachmentSenders) {
            sender.setCallback(callback);
        }
        return this;
    }

    public AtlasMessageComposer setTypeface(Typeface typeface) {
        this.mTypeFace = typeface;
        applyTypeface();
        return this;
    }

    /**
     * Must be called from Activity's onActivityResult to allow attachment senders to manage results
     * from e.g. selecting a gallery photo or taking a camera image.
     *
     * @param activity    Activity receiving the result.
     * @param requestCode Request code from the Activity's onActivityResult.
     * @param resultCode  Result code from the Activity's onActivityResult.
     * @param data        Intent data from the Activity's onActivityResult.
     * @return this AtlasMessageComposer.
     */
    public AtlasMessageComposer onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        for (AttachmentSender sender : mAttachmentSenders) {
            sender.onActivityResult(activity, requestCode, resultCode, data);
        }
        return this;
    }

    /**
     * Must be called from Activity's onRequestPermissionsResult to allow attachment senders to
     * manage dynamic permisttions.
     *
     * @param requestCode  The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (AttachmentSender sender : mAttachmentSenders) {
            sender.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mAttachButton != null) mAttachButton.setEnabled(enabled);
        if (mMessageEditText != null) mMessageEditText.setEnabled(enabled);
        if (mSendButton != null) {
            mSendButton.setEnabled(enabled && (mMessageEditText != null) && (mMessageEditText.getText().length() > 0));
        }
        super.setEnabled(enabled);
    }

    private void parseStyle(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AtlasMessageComposer, R.attr.AtlasMessageComposer, defStyle);
        mEnabled = ta.getBoolean(R.styleable.AtlasMessageComposer_android_enabled, true);
        this.mTextColor = ta.getColor(R.styleable.AtlasMessageComposer_inputTextColor, context.getResources().getColor(R.color.atlas_text_black));
        this.mTextSize = ta.getDimensionPixelSize(R.styleable.AtlasMessageComposer_inputTextSize, context.getResources().getDimensionPixelSize(R.dimen.atlas_text_size_input));
        this.mTextStyle = ta.getInt(R.styleable.AtlasMessageComposer_inputTextStyle, Typeface.NORMAL);
        String typeFaceName = ta.getString(R.styleable.AtlasMessageComposer_inputTextTypeface);
        this.mTypeFace = typeFaceName != null ? Typeface.create(typeFaceName, mTextStyle) : null;
        this.mUnderlineColor = ta.getColor(R.styleable.AtlasMessageComposer_inputUnderlineColor, context.getResources().getColor(R.color.atlas_color_primary_blue));
        this.mCursorColor = ta.getColor(R.styleable.AtlasMessageComposer_inputCursorColor, context.getResources().getColor(R.color.atlas_color_primary_blue));
        this.mAttachmentSendersBackground = ta.getDrawable(R.styleable.AtlasMessageComposer_attachmentSendersBackground);
        if (mAttachmentSendersBackground == null) {
            mAttachmentSendersBackground = ContextCompat.getDrawable(context, R.drawable.atlas_popup_background);
        }
        ta.recycle();
    }

    private void applyStyle() {
        setEnabled(mEnabled);

        mMessageEditText.setTextColor(mTextColor);
        mMessageEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        EditTextUtil.setCursorDrawableColor(mMessageEditText, mCursorColor);
        EditTextUtil.setUnderlineColor(mMessageEditText, mUnderlineColor);

        ColorStateList list = getResources().getColorStateList(R.color.atlas_message_composer_attach_button);
        Drawable d = DrawableCompat.wrap(mAttachButton.getDrawable().mutate());
        DrawableCompat.setTintList(d, list);
        mAttachButton.setImageDrawable(d);
    }

    private void applyTypeface() {
        mMessageEditText.setTypeface(mTypeFace, mTextStyle);
    }

    private void searchSmartOrTrendingGifs() {
        if (mPresenter == null) {
            return;
        }

        /*
         * Don't append new gifs to the existing ones if:
         * (1) user type in new character
         * (2) user receive new message
         */
        final boolean isAppend = !sTextChanged && !SmartGifsUtils.isMessageChanged();
        if (!isAppend) {
            mNextPageId = StringConstant.EMPTY;
        }

        if (sSearchGifsCall != null) {
            sSearchGifsCall.cancel();
        }

        if (!TextUtils.isEmpty(SmartGifsUtils.getSearchQuery())) {
            sSearchGifsCall = mPresenter.search(SmartGifsUtils.getSearchQuery(),
                    AbstractLocaleUtils.getCurrentLocaleName(getContext()), 24, mNextPageId, null, isAppend);
        } else {
            sSearchGifsCall = mPresenter.getTrending(24, mNextPageId, null, isAppend);
        }
    }

    private void addAttachmentMenuItem(AttachmentSender sender) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout menuLayout = (LinearLayout) mAttachmentMenu.getContentView();

        View menuItem = inflater.inflate(R.layout.atlas_message_composer_attachment_menu_item, menuLayout, false);
        ((TextView) menuItem.findViewById(R.id.title)).setText(sender.getTitle());
        menuItem.setTag(sender);
        menuItem.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                mAttachmentMenu.dismiss();
                if (v.getTag() instanceof GifSender) {
                    searchSmartOrTrendingGifs();
                } else {
                    ((AttachmentSender) v.getTag()).requestSend();
                }
            }
        });
        if (sender.getIcon() != null) {
            ImageView iconView = ((ImageView) menuItem.findViewById(R.id.icon));
            iconView.setImageResource(sender.getIcon());
            iconView.setVisibility(VISIBLE);
            Drawable d = DrawableCompat.wrap(iconView.getDrawable());
            DrawableCompat.setTint(d, getResources().getColor(R.color.atlas_icon_enabled));
        }
        menuLayout.addView(menuItem);
    }

    private void initAttachmentMenu(Context context, AttributeSet attrs, int defStyle) {
        if (mAttachmentMenu != null) throw new IllegalStateException("Already initialized menu");

        if (attrs == null) {
            mAttachmentMenu = new PopupWindow(context);
        } else {
            mAttachmentMenu = new PopupWindow(context, attrs, defStyle);
        }
        mAttachmentMenu.setContentView(LayoutInflater.from(context).inflate(R.layout.atlas_message_composer_attachment_menu, null));
        mAttachmentMenu.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAttachmentMenu.setOutsideTouchable(true);
        mAttachmentMenu.setBackgroundDrawable(mAttachmentSendersBackground);
        mAttachmentMenu.setFocusable(true);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (mAttachmentSenders.isEmpty()) return superState;
        SavedState savedState = new SavedState(superState);
        for (AttachmentSender sender : mAttachmentSenders) {
            Parcelable parcelable = sender.onSaveInstanceState();
            if (parcelable == null) continue;
            savedState.put(sender.getClass(), parcelable);
        }
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        for (AttachmentSender sender : mAttachmentSenders) {
            Parcelable parcelable = savedState.get(sender.getClass());
            if (parcelable == null) continue;
            sender.onRestoreInstanceState(parcelable);
        }
    }

    @Override
    public void onReceiveSearchResultsSucceed(GifsResponse response, boolean isAppend) {
        sTextChanged = false;
        SmartGifsUtils.resetMessageChanged();
        List<AbstractRVItem> items = new ArrayList<>();

        for (Result result : AbstractListUtils.shuffle(response.getResults())) {
            items.add(new ResultRVItem(GifAdapter.TYPE_GIF_ITEM, result));
        }
        mAdapter.insert(items, isAppend);
        mNextPageId = response.getNext();
    }

    @Override
    public void onReceiveSearchResultsFailed(BaseError error) {
        sTextChanged = false;
        SmartGifsUtils.resetMessageChanged();
    }

    @Override
    public void onReceiveTrendingSucceeded(List<Result> list, String nextPageId, boolean isAppend) {
        sTextChanged = false;
        SmartGifsUtils.resetMessageChanged();
        List<AbstractRVItem> items = new ArrayList<>();

        for (Result result : AbstractListUtils.shuffle(list)) {
            items.add(new ResultRVItem(GifAdapter.TYPE_GIF_ITEM, result));
        }
        mAdapter.insert(items, isAppend);
        mNextPageId = nextPageId;
    }

    @Override
    public void onReceiveTrendingFailed(BaseError error) {
        sTextChanged = false;
        SmartGifsUtils.resetMessageChanged();
    }

    /**
     * Saves a map from AttachmentSender class to AttachmentSender saved instance.
     */
    private static class SavedState extends BaseSavedState {
        Bundle mBundle = new Bundle();

        public SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState put(Class<? extends AttachmentSender> cls, Parcelable parcelable) {
            mBundle.putParcelable(cls.getName(), parcelable);
            return this;
        }

        Parcelable get(Class<? extends AttachmentSender> cls) {
            return mBundle.getParcelable(cls.getName());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(mBundle);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
            mBundle = in.readBundle();
        }
    }
}
