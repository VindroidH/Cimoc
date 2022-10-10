package com.haleydu.cimoc.helper;

import com.haleydu.cimoc.BuildConfig;
import com.haleydu.cimoc.manager.PreferenceManager;
import com.haleydu.cimoc.model.Comic;
import com.haleydu.cimoc.model.ComicDao;
import com.haleydu.cimoc.model.DaoSession;
import com.haleydu.cimoc.model.Source;
import com.haleydu.cimoc.source.Animx2;
import com.haleydu.cimoc.source.BaiNian;
import com.haleydu.cimoc.source.CCMH;
import com.haleydu.cimoc.source.Cartoonmad;
import com.haleydu.cimoc.source.ChuiXue;
import com.haleydu.cimoc.source.CopyMH;
import com.haleydu.cimoc.source.DM5;
import com.haleydu.cimoc.source.Dmzj;
import com.haleydu.cimoc.source.DmzjFix;
import com.haleydu.cimoc.source.Dmzjv2;
import com.haleydu.cimoc.source.EHentai;
import com.haleydu.cimoc.source.GuFeng;
import com.haleydu.cimoc.source.HHAAZZ;
import com.haleydu.cimoc.source.Hhxxee;
import com.haleydu.cimoc.source.HotManga;
import com.haleydu.cimoc.source.IKanman;
import com.haleydu.cimoc.source.JMTT;
import com.haleydu.cimoc.source.MH160;
import com.haleydu.cimoc.source.MH50;
import com.haleydu.cimoc.source.MH517;
import com.haleydu.cimoc.source.MH57;
import com.haleydu.cimoc.source.MHLove;
import com.haleydu.cimoc.source.ManHuaDB;
import com.haleydu.cimoc.source.MangaBZ;
import com.haleydu.cimoc.source.MangaNel;
import com.haleydu.cimoc.source.Mangakakalot;
import com.haleydu.cimoc.source.Manhuatai;
import com.haleydu.cimoc.source.MiGu;
import com.haleydu.cimoc.source.Ohmanhua;
import com.haleydu.cimoc.source.PuFei;
import com.haleydu.cimoc.source.QiManWu;
import com.haleydu.cimoc.source.QiMiaoMH;
import com.haleydu.cimoc.source.SixMH;
import com.haleydu.cimoc.source.Tencent;
import com.haleydu.cimoc.source.TuHao;
import com.haleydu.cimoc.source.U17;
import com.haleydu.cimoc.source.Webtoon;
import com.haleydu.cimoc.source.WebtoonDongManManHua;
import com.haleydu.cimoc.source.YKMH;
import com.haleydu.cimoc.source.YYLS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hiroshi on 2017/1/18.
 */

public class UpdateHelper {

    // 1.04.08.008
    private static final int VERSION = BuildConfig.VERSION_CODE;

    public static void update(PreferenceManager manager, final DaoSession session) {
        int version = manager.getInt(PreferenceManager.PREF_APP_VERSION, 0);
        if (version != VERSION) {
            initSource(session);
            manager.putInt(PreferenceManager.PREF_APP_VERSION, VERSION);
        }
    }

    /**
     * app: 1.4.8.0 -> 1.4.8.1
     * 删除本地漫画中 download 字段的值
     */
    private static void deleteDownloadFromLocal(final DaoSession session) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                ComicDao dao = session.getComicDao();
                List<Comic> list = dao.queryBuilder().where(ComicDao.Properties.Local.eq(true)).list();
                if (!list.isEmpty()) {
                    for (Comic comic : list) {
                        comic.setDownload(null);
                    }
                    dao.updateInTx(list);
                }
            }
        });
    }

    /**
     * 初始化图源
     */
    private static void initSource(DaoSession session) {
        List<Source> list = new ArrayList<>();
        list.add(IKanman.getDefaultSource());
        list.add(Dmzj.getDefaultSource());
        list.add(HHAAZZ.getDefaultSource());
        list.add(U17.getDefaultSource());
        list.add(DM5.getDefaultSource());
        list.add(Webtoon.getDefaultSource());
        list.add(MH57.getDefaultSource());
        list.add(MH50.getDefaultSource());
        list.add(Dmzjv2.getDefaultSource());
        list.add(MangaNel.getDefaultSource());
        list.add(Mangakakalot.getDefaultSource());
        list.add(PuFei.getDefaultSource());
        list.add(Cartoonmad.getDefaultSource());
        list.add(Animx2.getDefaultSource());
        list.add(MH517.getDefaultSource());
        list.add(BaiNian.getDefaultSource());
        list.add(MiGu.getDefaultSource());
        list.add(Tencent.getDefaultSource());
        list.add(EHentai.getDefaultSource());
        list.add(QiManWu.getDefaultSource());
        list.add(Hhxxee.getDefaultSource());
        list.add(ChuiXue.getDefaultSource());
        list.add(BaiNian.getDefaultSource());
        list.add(TuHao.getDefaultSource());
        list.add(SixMH.getDefaultSource());
        list.add(MangaBZ.getDefaultSource());
        list.add(ManHuaDB.getDefaultSource());
        list.add(Manhuatai.getDefaultSource());
        list.add(GuFeng.getDefaultSource());
        list.add(CCMH.getDefaultSource());
        list.add(Manhuatai.getDefaultSource());
        list.add(MHLove.getDefaultSource());
        list.add(GuFeng.getDefaultSource());
        list.add(YYLS.getDefaultSource());
        list.add(JMTT.getDefaultSource());
        list.add(Ohmanhua.getDefaultSource());
        list.add(CopyMH.getDefaultSource());
        list.add(HotManga.getDefaultSource());
        list.add(WebtoonDongManManHua.getDefaultSource());
        list.add(MH160.getDefaultSource());
        list.add(QiMiaoMH.getDefaultSource());
        list.add(YKMH.getDefaultSource());
        list.add(DmzjFix.getDefaultSource());
        session.getSourceDao().insertOrReplaceInTx(list);
    }
}
