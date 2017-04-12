package com.example.ervin.google_map_test.DB;

import android.provider.BaseColumns;

/**
 * Created by felix on 4/9/2017.
 */

public final class LocationsContract {

    private LocationsContract() {}

    public static class LocationsEntry implements BaseColumns {
        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_LOCATION_NAME = "locationName";
        public static final String COLUMN_LOCATION_TYPE = "locationType";
        public static final String COLUMN_COORDINATES = "coordinates";
    }

    public static class PlansEntry implements BaseColumns {
        public static final String TABLE_NAME = "plans";
    }

    public static class PlanLocationRelationsEntry implements BaseColumns {
        public static final String TABLE_NAME = "planLocationRelations";
        public static final String COLUMN_PLAN_ID = "planId";
        public static final String COLUMN_LOCATION_ID = "locationId";
        public static final String COLUMN_SEQ = "seq";
    }
}
