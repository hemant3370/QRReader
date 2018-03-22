package hemant3370.com.qrreader.Storage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleRecord(QRModel qrModel);

    @Query("SELECT * FROM QRCodes")
    List<QRModel> fetchAllData();

    @Query("DELETE FROM QRCodes WHERE text =:qr_text")
    void deleteQRCode(String qr_text);

}
