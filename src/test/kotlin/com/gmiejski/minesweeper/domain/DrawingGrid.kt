package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.grid.BOMB
import com.gmiejski.minesweeper.game.domain.grid.BoardView
import com.gmiejski.minesweeper.game.domain.grid.UNKNOWN
import com.gmiejski.minesweeper.game.domain.grid.SAFE_FIELD

class DrawGrid {
    fun draw(view: BoardView) {
        view.gridRows.forEach {
            print("____".repeat(it.fields.size))
            println()
            it.fields.forEach {
                print("|")
                if (it.state == BOMB) {
                    print(" * ")
                } else if (it.state == SAFE_FIELD) {
                    print("   ")
                } else if (it.state == UNKNOWN) {
                    print("###")
                } else {
                    print("#${it.state}#")
                }
            }
            println()
            print("````".repeat(it.fields.size))
            println()
        }
    }
}