// FIXME: license file, or use the -l option to generate the files with the header.

package vendor.sprd.hardware.log;

import vendor.sprd.hardware.log.ILogCallback;

interface ILogControl {
    String sendCmd(in String desSocket, in String cmd);
    void setCallback(in ILogCallback callback);
}