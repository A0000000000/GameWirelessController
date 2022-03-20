package xyz.a00000.joystickcustomview.bean

class Xbox360Type {

    companion object {

        @JvmStatic
        val KEY = 0
        @JvmStatic
        val AXIS = 1
        @JvmStatic
        val TRIGGER = 2

        class KeyType {
            companion object {
                @JvmStatic
                val A = 0
                @JvmStatic
                val B = 1
                @JvmStatic
                val X = 2
                @JvmStatic
                val Y = 3

                @JvmStatic
                val LEFT = 4
                @JvmStatic
                val TOP = 5
                @JvmStatic
                val RIGHT = 6
                @JvmStatic
                val BOTTOM = 7

                @JvmStatic
                val LEFT_BUTTON = 8
                @JvmStatic
                val RIGHT_BUTTON = 9
                @JvmStatic
                val RIGHT_ROCKER = 10
                @JvmStatic
                val LEFT_ROCKER = 11

                @JvmStatic
                val MENU = 12
                @JvmStatic
                val VIEW = 13
                @JvmStatic
                val MAIN = 14
                @JvmStatic
                val FUNCTION = 15
            }
        }

        class AxisType {
            companion object {
                @JvmStatic
                val LEFT_ROCKER = 0
                @JvmStatic
                val RIGHT_ROCKER = 1
            }
        }

        class TriggerType {
            companion object {
                @JvmStatic
                val LEFT_TRIGGER = 0
                @JvmStatic
                val RIGHT_TRIGGER = 1
            }
        }

    }

}