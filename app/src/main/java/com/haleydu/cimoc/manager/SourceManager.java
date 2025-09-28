package com.haleydu.cimoc.manager;

import android.util.Log;
import android.util.SparseArray;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.Source;
import com.haleydu.cimoc.model.SourceDao;
import com.haleydu.cimoc.model.Source_;
import com.haleydu.cimoc.parser.Parser;
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
import com.haleydu.cimoc.source.Locality;
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
import com.haleydu.cimoc.source.Null;
import com.haleydu.cimoc.source.Ohmanhua;
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

import java.util.List;

import okhttp3.Headers;
import rx.Observable;

/**
 * Created by Hiroshi on 2016/8/11.
 */
public class SourceManager {
    private final static String TAG = "Cimoc-SourceManager";
    private static volatile SourceManager mInstance;

    private final SourceDao mSourceDao;
    private final SparseArray<Parser> mParserArray = new SparseArray<>();

    private SourceManager(AppGetter getter) {
        mSourceDao = getter.getAppInstance().getDaoSession().getSourceDao();
    }

    public static SourceManager getInstance(AppGetter getter) {
        if (mInstance == null) {
            synchronized (SourceManager.class) {
                if (mInstance == null) {
                    mInstance = new SourceManager(getter);
                }
            }
        }
        return mInstance;
    }

    public Observable<List<Source>> list() {
        Log.d(TAG, "[list]");
//        mSourceDao.queryBuilder().orderAsc(Properties.Type).rx().list();
        return Observable.fromCallable(() ->
                mSourceDao.getBox()
                        .query()
                        .order(Source_.type)
                        .build()
                        .find()
        );
    }

    public Observable<List<Source>> listEnableInRx() {
        Log.d(TAG, "[listEnableInRx]");
        /*
        return mSourceDao.queryBuilder()
                .where(Properties.Enable.equal(true))
                .orderAsc(Properties.Type)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mSourceDao.getBox()
                        .query()
                        .equal(Source_.enable, true)
                        .order(Source_.type)
                        .build()
                        .find()
        );
    }

    public List<Source> listEnable() {
        Log.d(TAG, "[listEnable]");
        return mSourceDao.getBox()
                .query()
                .equal(Source_.enable, true)
                .order(Source_.type)
                .build()
                .find();
    }

    public Source load(int type) {
        Log.d(TAG, "[load] type: " + type);
        /*
        return mSourceDao.queryBuilder()
                .where(Properties.Type.equal(type))
                .unique();
         */
        return mSourceDao.getBox()
                .query()
                .equal(Source_.type, true)
                .build()
                .findUnique();
    }

    public long insert(Source source) {
        Log.d(TAG, "[insert] source: " + source);
        return mSourceDao.insert(source);
    }

    public void update(Source source) {
        Log.d(TAG, "[update] source: " + source);
        mSourceDao.update(source);
    }

    public Parser getParser(int type) {
        Parser parser = mParserArray.get(type);
        if (parser == null) {
            Source source = load(type);
            parser = switch (type) {
                case IKanman.TYPE -> new IKanman(source);
                case Dmzj.TYPE -> new Dmzj(source);
                case HHAAZZ.TYPE -> new HHAAZZ(source);
                case U17.TYPE -> new U17(source);
                case DM5.TYPE -> new DM5(source);
                case Webtoon.TYPE -> new Webtoon(source);
                case MH57.TYPE -> new MH57(source);
                case MH50.TYPE -> new MH50(source);
                case Dmzjv2.TYPE -> new Dmzjv2(source);
                case Locality.TYPE -> new Locality();
                case MangaNel.TYPE -> new MangaNel(source);
                case Tencent.TYPE -> new Tencent(source);
                case EHentai.TYPE -> new EHentai(source);
                case QiManWu.TYPE -> new QiManWu(source);
                case Hhxxee.TYPE -> new Hhxxee(source);
                case Cartoonmad.TYPE -> new Cartoonmad(source);
                case Animx2.TYPE -> new Animx2(source);
                case MH517.TYPE -> new MH517(source);
                case MiGu.TYPE -> new MiGu(source);
                case BaiNian.TYPE -> new BaiNian(source);
                case ChuiXue.TYPE -> new ChuiXue(source);
                case TuHao.TYPE -> new TuHao(source);
                case SixMH.TYPE -> new SixMH(source);
                case ManHuaDB.TYPE -> new ManHuaDB(source);
                case Manhuatai.TYPE -> new Manhuatai(source);
                case GuFeng.TYPE -> new GuFeng(source);
                case CCMH.TYPE -> new CCMH(source);
                case MHLove.TYPE -> new MHLove(source);
                case YYLS.TYPE -> new YYLS(source);
                case JMTT.TYPE -> new JMTT(source);
                case Mangakakalot.TYPE -> new Mangakakalot(source);
                case Ohmanhua.TYPE -> new Ohmanhua(source);
                case CopyMH.TYPE -> new CopyMH(source);
                case HotManga.TYPE -> new HotManga(source);
                case MangaBZ.TYPE -> new MangaBZ(source);
                case WebtoonDongManManHua.TYPE -> new WebtoonDongManManHua(source);
                case MH160.TYPE -> new MH160(source);
                case QiMiaoMH.TYPE -> new QiMiaoMH(source);
                case YKMH.TYPE -> new YKMH(source);
                case DmzjFix.TYPE -> new DmzjFix(source);
                default -> new Null();
            };
            mParserArray.put(type, parser);
        }
        return parser;
    }

    public class TitleGetter {

        public String getTitle(int type) {
            return getParser(type).getTitle();
        }

    }

    public class HeaderGetter {

        public Headers getHeader(int type) {
            return getParser(type).getHeader();
        }

    }
}
