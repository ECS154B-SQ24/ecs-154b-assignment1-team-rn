// This file contains ALU control logic.

package dinocpu.components

import chisel3._
import chisel3.util._

/**
 * The ALU control unit
 *
 * Input:  aluop        Specifying the type of instruction using ALU
 *                          . 0 for none of the below
 *                          . 1 for 64-bit R-type
 *                          . 2 for 64-bit I-type
 *                          . 3 for 32-bit R-type
 *                          . 4 for 32-bit I-type
 *                          . 5 for non-arithmetic instruction types that uses ALU (auipc/jal/jarl/Load/Store)
 * Input:  funct7       The most significant bits of the instruction.
 * Input:  funct3       The middle three bits of the instruction (12-14).
 *
 * Output: operation    What we want the ALU to do.
 *
 * For more information, see Section 4.4 and A.5 of Patterson and Hennessy.
 * This is loosely based on figure 4.12
 */


class ALUControl extends Module {
  val io = IO(new Bundle {
    val aluop     = Input(UInt(3.W))
    val funct7    = Input(UInt(7.W))
    val funct3    = Input(UInt(3.W))

    val operation = Output(UInt(5.W))
  })

  // Default invalid operation
  io.operation := "b11111".U

//DONT USE WHENS WITHIN SWITCHES!!

  when(io.aluop === "b001".U) { // For 64-bit R-type instructions
    when(io.funct7 === "b0000001".U) { // M
      switch(io.funct3) {
        is("b000".U) { io.operation := "b00110".U } // MUL
        is("b001".U) { io.operation := "b00111".U } // MULH
        is("b010".U) { io.operation := "b11000".U } // MULHSU
        is("b011".U) { io.operation := "b01000".U } // MULHU
        is("b100".U) { io.operation := "b01011".U } // DIV
        is("b101".U) { io.operation := "b01010".U } // DIVU
        is("b110".U) { io.operation := "b11100".U } // REM
        is("b111".U) { io.operation := "b11011".U } // REMU
      }
    } .otherwise { // Non-M
      switch(io.funct3) {
        is("b000".U) {
          when(io.funct7 === "b0100000".U) {
            io.operation := "b00100".U // SUB
          } .otherwise {
            io.operation := "b00001".U // ADD
          }
        }
        is("b001".U) { io.operation := "b10010".U } // SLL
        is("b010".U) { io.operation := "b10110".U } // SLT
        is("b011".U) { io.operation := "b10111".U } // SLTU
        is("b100".U) { io.operation := "b01111".U } // XOR
        is("b101".U) {
          when(io.funct7 === "b0100000".U) {
            io.operation := "b10000".U // SRA
          } .otherwise {
            io.operation := "b10100".U // SRL
          }
        }
        is("b110".U) { io.operation := "b01110".U } // OR
        is("b111".U) { io.operation := "b01101".U } // AND
      }
    }
  }

  when(io.aluop === "b011".U) { // For 32-bit R-type instructions
    when(io.funct7 === "b0000001".U) { // M
      switch(io.funct3) {
        is("b000".U) { io.operation := "b00101".U } // MULW
        is("b100".U) { io.operation := "b01001".U } // DIVW
        is("b101".U) { io.operation := "b01100".U } // DIVUW
        is("b110".U) { io.operation := "b11010".U } // REMW
        is("b111".U) { io.operation := "b11001".U } // REMUW
      }
    } .otherwise { // Non-M
      switch(io.funct3) {
        is("b000".U) {
          when(io.funct7 === "b0100000".U) {
            io.operation := "b00010".U // SUBW
          } .otherwise {
            io.operation := "b00000".U // ADDW
          }
        }
        is("b001".U) { io.operation := "b10011".U } // SLLW
        is("b101".U) {
          when(io.funct7 === "b0100000".U) {
            io.operation := "b10001".U // SRAW
          } .otherwise {
            io.operation := "b10101".U // SRLW
          }
        }
      }
    }
  }
}
