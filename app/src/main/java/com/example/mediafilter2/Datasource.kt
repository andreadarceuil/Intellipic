package com.example.mediafilter2

class Datasource() {

    fun loadDashboardItems(): List<DashboardItem> {
        return mutableListOf<DashboardItem>(
                DashboardItem(R.string.item1Title,R.string.item1Description, R.drawable.image1),
                DashboardItem(R.string.item2Title, R.string.item2Description,  R.drawable.image2),
            DashboardItem(R.string.item3Title, R.string.item3Description,  R.drawable.image3),
            DashboardItem(R.string.item4Title, R.string.item4Description,  R.drawable.image4),

        )
    }
}