import com.TrainRepairServiceApplication;
import com.ydzbinfo.emis.guns.config.HighLevelRepairProperties;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.HighLevelRepairInfo;
import com.ydzbinfo.emis.utils.HttpUtil;
import org.apache.axis2.AxisFault;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 张天可
 * @date 2021/6/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TrainRepairServiceApplication.class})
public class ApplicationTest {


    @Autowired
    HighLevelRepairProperties highLevelRepairProperties;

    @Test
    public void test() throws AxisFault {
        List<HighLevelRepairInfo> highLevelRepairInfoList = HttpUtil.getHighLevelRepairInfo(highLevelRepairProperties, "018");
        System.out.println(highLevelRepairInfoList);
    }
}
