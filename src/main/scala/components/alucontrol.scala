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

  // Set a default operation code that indicates an invalid operation
  io.operation := "b11111".U 

  switch(io.aluop) {
    is("b001".U) { // Handling 64-bit R-type instructions
      switch(io.funct3) {
        is("b000".U) {
          when(io.funct7 === "b0000001".U) { io.operation := "b00110".U } // MUL
          .elsewhen(io.funct7 === "b0000000".U) { io.operation := "b00001".U } // ADD
          .elsewhen(io.funct7 === "b0100000".U) { io.operation := "b00100".U } // SUB
        }
        is("b001".U) { io.operation := "b10010".U } // SLL (Shift Left Logical)
        is("b010".U) { io.operation := "b10110".U } // SLT (Set Less Than)
        is("b011".U) { io.operation := "b10111".U } // SLTU (Set Less Than Unsigned)
        is("b100".U) { io.operation := "b01111".U } // XOR
        is("b101".U) {
          when(io.funct7 === "b0000000".U) { io.operation := "b10100".U } // SRL (Shift Right Logical)
          .elsewhen(io.funct7 === "b0100000".U) { io.operation := "b10000".U } // SRA (Shift Right Arithmetic)
        }
        is("b110".U) { io.operation := "b01110".U } // OR
        is("b111".U) { io.operation := "b01101".U } // AND
      }
    }

    is("b011".U) { // Handling 32-bit R-type instructions
      switch(io.funct3) {
        is("b000".U) {
          when(io.funct7 === "b0000001".U) { io.operation := "b00101".U } // MULW
          .elsewhen(io.funct7 === "b0000000".U) { io.operation := "b00000".U } // ADDW
          .elsewhen(io.funct7 === "b0100000".U) { io.operation := "b00010".U } // SUBW
        }
        is("b001".U) { io.operation := "b10011".U } // SLLW (Shift Left Logical Word)
        is("b101".U) {
          when(io.funct7 === "b0000000".U) { io.operation := "b10101".U } // SRLW (Shift Right Logical Word)
          .elsewhen(io.funct7 === "b0100000".U) { io.operation := "b10001".U } // SRAW (Shift Right Arithmetic Word)
        }
      }
    }
  }
}


