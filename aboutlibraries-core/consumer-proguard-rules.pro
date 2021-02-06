# Ensure we can discover the resources
-keepclasseswithmembers class **.R$* {
    public static final int define_*;
}