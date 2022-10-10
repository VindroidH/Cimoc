package com.haleydu.cimoc.utils;

import com.haleydu.cimoc.model.Comic;
import com.haleydu.cimoc.source.CCMH;
import com.haleydu.cimoc.source.Cartoonmad;
import com.haleydu.cimoc.source.CopyMH;
import com.haleydu.cimoc.source.HotManga;
import com.haleydu.cimoc.source.JMTT;
import com.haleydu.cimoc.source.MH50;
import com.haleydu.cimoc.source.Manhuatai;
import com.haleydu.cimoc.source.MiGu;
import com.haleydu.cimoc.source.Tencent;

public class interpretationUtils {

    public static boolean isReverseOrder(Comic comic) {
        int type = comic.getSource();
        return type == MH50.TYPE ||
                type == CCMH.TYPE ||
                type == Cartoonmad.TYPE ||
                type == CopyMH.TYPE ||
                type == HotManga.TYPE ||
                type == MiGu.TYPE ||
                type == Manhuatai.TYPE ||
                type == Tencent.TYPE ||
                type == JMTT.TYPE;
    }
}

