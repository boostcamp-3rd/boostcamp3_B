package com.swsnack.catchhouse.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.swsnack.catchhouse.R;
import com.swsnack.catchhouse.adapter.roomadapter.RoomListAdapter;
import com.swsnack.catchhouse.data.model.Room;
import com.swsnack.catchhouse.databinding.DialogChangeNickNameBinding;
import com.swsnack.catchhouse.databinding.DialogChangePasswordBinding;
import com.swsnack.catchhouse.databinding.FragmentMyPageBinding;
import com.swsnack.catchhouse.util.DataConverter;
import com.swsnack.catchhouse.view.BaseFragment;
import com.swsnack.catchhouse.view.activitity.PostActivity;
import com.swsnack.catchhouse.viewmodel.userviewmodel.UserViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getCacheDir;
import static com.swsnack.catchhouse.Constant.GALLERY;
import static com.swsnack.catchhouse.Constant.INTENT_ROOM;
import static com.swsnack.catchhouse.Constant.SignInMethod.FACEBOOK;
import static com.swsnack.catchhouse.Constant.SignInMethod.GOOGLE;
import static com.swsnack.catchhouse.Constant.Ucrop.UCROP_HEIGHT_MAX;
import static com.swsnack.catchhouse.Constant.Ucrop.UCROP_HEIGHT_RATIO;
import static com.swsnack.catchhouse.Constant.Ucrop.UCROP_WIDTH_MAX;
import static com.swsnack.catchhouse.Constant.Ucrop.UCROP_WIDTH_RATIO;

public class MyPageFragment extends BaseFragment<FragmentMyPageBinding, UserViewModel> {

    @Override
    protected int getLayout() {
        return R.layout.fragment_my_page;
    }

    @Override
    protected Class<UserViewModel> getViewModelClass() {
        return UserViewModel.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().setHandler(getViewModel());
        getViewModel().getUserData();

        getBinding().ctlMyPage.setExpandedTitleColor(Color.TRANSPARENT);
        getBinding().ctlMyPage.setCollapsedTitleTextColor(Color.WHITE);

        for (String signInMethod : FirebaseAuth.getInstance().getCurrentUser().getProviders()) {
            if (signInMethod.equals(FACEBOOK) || signInMethod.equals(GOOGLE)) {
                getBinding().tvMyPageChangePassword.setVisibility(View.GONE);
                break;
            }
        }

        RoomListAdapter favoriteRoomListAdapter = new RoomListAdapter(getContext(), getViewModel());
        getBinding().lyMyPageInclude.rvMyPageMyFavorite.setAdapter(favoriteRoomListAdapter);
        getBinding().lyMyPageInclude.rvMyPageMyFavorite.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        favoriteRoomListAdapter.setOnItemClickListener((viewHolder, position) -> {
            Room room = favoriteRoomListAdapter.getItem(position);
            startActivity(new Intent(getContext(), PostActivity.class).putExtra(INTENT_ROOM, room));
        });

        RoomListAdapter recentRoomListAdapter = new RoomListAdapter(getContext(), getViewModel());
        getBinding().lyMyPageInclude.rvMyPageRecentlyVisit.setAdapter(recentRoomListAdapter);
        getBinding().lyMyPageInclude.rvMyPageRecentlyVisit.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        getBinding().tvMyPageChangeNickName.setOnClickListener(v -> {
            DialogChangeNickNameBinding dialogBinding = DialogChangeNickNameBinding.inflate(getLayoutInflater());
            dialogBinding.setHandler(getViewModel());

            Dialog dialogChangeNickName = new Dialog(getContext());
            dialogChangeNickName.setContentView(dialogBinding.getRoot());
            dialogChangeNickName.show();

            dialogBinding.tvDialogChangeNickNameNegative.setOnClickListener(negative -> dialogChangeNickName.dismiss());
            dialogBinding.tvDialogChangeNickNamePositive.setOnClickListener(positive -> {
                getViewModel().changeNickName(dialogBinding.etDialogChangeNickName.getText().toString());
                dialogChangeNickName.dismiss();
            });
        });

        getBinding().tvMyPageChangePassword.setOnClickListener(v -> {
            DialogChangePasswordBinding dialogBinding = DialogChangePasswordBinding.inflate(getLayoutInflater());
            dialogBinding.setHandler(getViewModel());

            Dialog dialogChangePassword = new Dialog(getContext());
            dialogChangePassword.setContentView(dialogBinding.getRoot());
            dialogChangePassword.show();

            dialogBinding.tvDialogChangePasswordNegative.setOnClickListener(negative -> dialogChangePassword.dismiss());
            dialogBinding.tvDialogChangePasswordPositive.setOnClickListener(positive -> {
                if (!dialogBinding.etDialogChangePasswordNewPassword.getText().toString()
                        .equals(dialogBinding.etDialogChangePasswordNewPasswordConfirm.getText().toString())) {
                    Snackbar.make(getBinding().getRoot(), R.string.snack_wrong_password, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                getViewModel().updatePassword(dialogBinding.etDialogChangePasswordExPassword.getText().toString(),
                        dialogBinding.etDialogChangePasswordNewPassword.getText().toString());

                dialogChangePassword.dismiss();
            });
        });

        getBinding().tvMyPageChangeProfile.setOnClickListener(v -> startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), GALLERY));

        getBinding().lyMyPageInclude.tvMyPageRecentlyVisitSubTitle.setVisibility(View.GONE);
        getBinding().lyMyPageInclude.tvMyPageMyFavoriteSubTitle.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getViewModel().getFavoriteRoom();
        getViewModel().getRecentRoom();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY) {
                Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cache_profile.jpeg"));

                UCrop.of(data.getData(), destinationUri)
                        .withAspectRatio(UCROP_WIDTH_RATIO, UCROP_HEIGHT_RATIO)
                        .withMaxResultSize(UCROP_WIDTH_MAX, UCROP_HEIGHT_MAX)
                        .start(getActivity(), this);

            } else if (requestCode == UCrop.REQUEST_CROP) {
                final Uri resultUri = UCrop.getOutput(data);
                getViewModel().updateProfile(resultUri);
            }
        }
    }
}
