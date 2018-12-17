package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
        // sent by TimerService, received by SellingService, APIService
public class TimeTickBroadcast implements Broadcast {

        private int time;

        public TimeTickBroadcast(int time) { this.time = time; }

        public int getTime() { return time; }
}
