###########################################################################
#
# Generated by : Version 10.1 Build 153 11/29/2010 SJ Web Edition
#
# Project      : jop
# Revision     : jop
#
# Date         : Wed Feb 02 12:23:39 CET 2011
#
###########################################################################
 
 
# WARNING: Expected ENABLE_CLOCK_LATENCY to be set to 'ON', but it is set to 'OFF'
#          In SDC, create_generated_clock auto-generates clock latency
#
# ------------------------------------------
#
#
# ------------------------------------------


# Original Clock Setting Name: clk
create_clock -period 20 -waveform {0 6} -name clk [get_ports clk]
