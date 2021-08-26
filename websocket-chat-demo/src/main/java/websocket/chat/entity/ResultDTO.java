package websocket.chat.entity;

/**
 * @author Ricky Fung
 */
public class ResultDTO<T> {
    private int code;
    private String msg;
    private T data;

    public static ResultDTO ok() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(1);
        resultDTO.setMsg("请求成功");
        return resultDTO;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
