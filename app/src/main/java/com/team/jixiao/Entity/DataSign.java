package com.team.jixiao.Entity;

import java.util.List;

/*
{
            "nickname": "阿浪",
            "clock_Recording": [
                {
                    "sign": 2,
                    "add_time": "2022-11-22 14:54:44",
                    "type": "正常"
                }
            ]
        },
        {
            "nickname": "浪浪鹏",
            "clock_Recording": [
                {
                    "sign": 1,
                    "add_time": "2022-11-22 15:06:42",
                    "type": "正常"
                },
                {
                    "sign": 2,
                    "add_time": "2022-11-22 15:06:42",
                    "type": "正常"
                }
            ]
        }
 */
public class DataSign {
    private String nickname;
    private List<Clock_Recording> clock_Recording;

    @Override
    public String toString() {
        return "DataSign{" +
                "nickname='" + nickname + '\'' +
                ", clock_Recording=" + clock_Recording +
                '}';
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<Clock_Recording> getClock_Recording() {
        return clock_Recording;
    }

    public void setClock_Recording(List<Clock_Recording> clock_Recording) {
        this.clock_Recording = clock_Recording;
    }

    public static class Clock_Recording {
        private int sign;
        private String add_time;
        private String type;

        @Override
        public String toString() {
            return "Clock_Recording{" +
                    "sign=" + sign +
                    ", add_time='" + add_time + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        public int getSign() {
            return sign;
        }

        public void setSign(int sign) {
            this.sign = sign;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
