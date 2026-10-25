package hemant3370.com.qrreader.Storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
