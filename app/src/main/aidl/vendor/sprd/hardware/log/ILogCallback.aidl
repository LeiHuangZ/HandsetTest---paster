// FIXME: license file, or use the -l option to generate the files with the header.

package vendor.sprd.hardware.log;

interface ILogCallback {
    // Adding return type to method instead of out param String ret since there is only one return value.
    String onCommand(in String cmd);
}