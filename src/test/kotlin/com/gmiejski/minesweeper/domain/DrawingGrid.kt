package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.grid.BoardView

class DrawGrid {
    fun draw(view: BoardView) {
        view.gridRows.forEach {
            print("____".repeat(it.fieldView.size))
            println()
            it.fieldView.forEach {
                print("|")
                if (it.discovered && it.isBomb) {
                    print(" * ")
                } else if (it.discovered) {
                    print("   ")
                } else {
                    print("###")
                }
            }
            println()
            print("````".repeat(it.fieldView.size))
            println()
        }
    }
}