package com.redhat.sie;

import java.util.Random;
import java.util.Map.Entry;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.util.CloseableIterator;

/* Sample Data:
    # signal source; rtu-id; linux timestamp; quality code; value

    RES_LINE$I_FROM_KA$013035;RTU000001;1546300800001;192;0.11795469432801944
    RES_SGEN$P_MW$000290;RTU000001;1546300800003;192;5.91
    RES_LINE$PL_MW$004869;RTU000001;1546300800004;192;0.025685402551038017
    RES_TRAFO$P_HV_MW$000648;RTU000001;1546300800005;192;122.72102141559058
    RES_BUS$P_MW$000028;RTU000001;1546300800007;192;0.0
    RES_LINE$VM_TO_PU$004394;RTU000001;1546300800008;192;1.0044594812544634
 */

class Rtu {
    String signalSource;
    String rtuId;
    long linuxTimestamp;
    long qualityCode;
    double value;
}

public class DanielTest {
    /*
     * # rtu-id; signal source; linux timestamp; quality code; value
     * RES_LINE$I_FROM_KA$013035;RTU000001;1546300800001;192;0.11795469432801944
     */

    private RemoteCache<String, String> cache;

    public void connectToCache(RemoteCacheManager rmc) {

        String cacheName = System.getenv("JDG_CACHE");
        if (cacheName == null || cacheName.length() == 0) {
            cacheName = "FTS";
        }

 
        cache = rmc.getCache(cacheName);
        if (cache == null) {
            throw new RuntimeException("Cache >" + cacheName + "< not found");
        }
    }

    public void createEntries() {
        Rtu val = new Rtu();
        Random rnd = new Random();

        String numEntriesStr = System.getenv("NUM_ENTRIES");
        if (numEntriesStr == null || numEntriesStr.length() == 0) {
            numEntriesStr = "1000000";
        }

        long numValues = Long.parseLong(numEntriesStr);
        long ts_start = System.currentTimeMillis();

        for (int i = 0; i < numValues; i++) {
            // val.signalSource = "RES_LINE$I_FROM_KA$"+String.format("%06d" , i);
            val.signalSource = "RES_LINE$I_FROM_KA$" + i;
            val.rtuId = "RTU000001";
            val.linuxTimestamp = System.currentTimeMillis();
            val.qualityCode = rnd.nextInt(256);
            val.value = rnd.nextDouble();

            // ToDO: use proto buf marshalling:
            String csv = val.signalSource + ";" + val.rtuId + ";" + val.linuxTimestamp + ";" + val.qualityCode + ";"
                    + val.value;

            cache.put(val.signalSource, csv);

            if (i % 50000 == 0) {
                System.out.print('.');
            }
        }
        long ts_stop = System.currentTimeMillis();
        long dur = ts_stop - ts_start;
        System.out.println();
        System.out.println("CREATE;" + numValues + ";took;" + dur + ";msec, that is ;"
                + (int) (numValues / (dur / 1000.0)) + "; entries per second");
    }

    public void dumpEntries() {

        // cache.retrieveEntries(filterConverterFactory, filterConverterParams,
        // segments, batchSize);

        StringBuffer sb = new StringBuffer(100 * 1024 * 1024);
        long numValues = 0;
        long ts_start = System.currentTimeMillis();

        /*
         * Approach 1+2: CloseableIteratorCollection<String> it = cache.values();
         */

        /*
         * Approach 1: for (String s : it) { //System.out.println(s); sb.append(s);
         * numValues++; if (numValues % 50000 == 0) { System.out.print('.'); } }
         */

        /*
         * Approach 2: Object[] array = it.toArray(); numValues = array.length;
         */

         /* Approach 3: */
        CloseableIterator<Entry<Object, Object>> it = cache.retrieveEntries(null, null, null, 10000);

        while (it.hasNext()) {
            Entry<Object, Object> e = it.next();

            sb.append(e.getValue().toString());
            numValues++;
            if (numValues % 50000 == 0) {
                System.out.print('.');
            }
        }

        long ts_stop = System.currentTimeMillis();
        long dur = ts_stop - ts_start;
        System.out.println();
        System.out.println(
                "DUMP;" + numValues + "; values took ;" + dur + "; msec, that is ;" + (int) (numValues / (dur / 1000.0))
                        + "; entries per second. size;" + sb.length() / 1024 / 1024 + ";MiB");
    }
}
