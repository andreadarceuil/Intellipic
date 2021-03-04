package com.example.mediafilter2

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class DashboardItem(
        @StringRes var stringResourceId1: Int,
        @StringRes var stringResourceId2: Int,
        @DrawableRes var imageResourceId: Int
)