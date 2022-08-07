function CardNode(figure, pattern, number) {

    this.figure = figure;
    this.pattern = pattern;
    this.number = number;
}

CardNode.prototype = {
    toString: function () {
        return "CardNode{" +
            "figure=" + this.figure +
            ", pattern=" + this.pattern +
            ", number=" + this.number +
            '}';
    }
}

function PiPei(type, onePiPei) {
    this.type = type;
    this.onePiPei = onePiPei;
    this.toString = function () {
        return "PiPei{" +
            "type=" + this.type +
            ", onePiPei=" + LSutil.arrToString(this.onePiPei) +
            '}';
    }
}

var huPai3 = {
    printFlag: false,
    printResult: false,
    piPeiArr: new Array(10),
    piPeiDeep: -1,
    pipeiTimes: [0],

    nowMatchGang: function (list, cardSum) {

        if (this.printFlag) {
            console.log("尝试杠")
        }

        var type = '杠';
        var onePiPei = new Array(3);
        var temp = list[0];
        for (var i = 0; i < 3; i++) {
            onePiPei[i] = temp.figure + temp.pattern * 9;
        }
        this.piPeiArr[this.piPeiDeep] = new PiPei(type, onePiPei);

        if (3 == cardSum) {
            return true;
        }
        if (this.pipeiTimes[0]-- > 32) {
            return false;
        }

        var nodeCopy = null;


        list[0].number -= 3;
        if (0 == list[0].number) {
            nodeCopy = list[0];
            list.splice(0, 1);
        }


        var SY_isHu = this.isMatchAble(list, cardSum - 3);

        if (nodeCopy != null) {
            list.splice(0, 0, nodeCopy);
        }
        list[0].number += 3;

        return SY_isHu;
    },

    nowMatchDuiZi: function (list, cardSum) {
        if (0 == cardSum % 3) {

            return false;
        }
        if (this.printFlag) {
            console.log("尝试对子")
        }

        var type = '对';
        var onePiPei = new Array(2);
        var temp = list[0];
        for (var i = 0; i < 2; i++) {
            onePiPei[i] = temp.figure + temp.pattern * 9;
        }
        this.piPeiArr[this.piPeiDeep] = new PiPei(type, onePiPei);

        if (2 == cardSum) {
            return true;
        }
        if (this.pipeiTimes[0]++ > 32) {
            return false;
        }

        var nodeCopy = null;


        list[0].number -= 2;
        if (0 == list[0].number) {
            nodeCopy = list[0];
            list.splice(0, 1);
        }


        var SY_isHu = this.isMatchAble(list, cardSum - 2);

        if (nodeCopy != null) {
            list.splice(0, 0, nodeCopy);
        }
        list[0].number += 2;

        return SY_isHu;
    },

    nowMatchShunZi: function
        (list,
         cardSum
        ) {

        if (list.length < 3) {
            return false;
        }


        for (var i = 0; i < 2; i++) {

            if (list[i].pattern != list[i + 1].pattern) {
                return false;
            } else if (list[i].figure != list[i + 1].figure - 1) {
                return false;
            }
        }

        if (this.printFlag) {
            console.log("尝试顺子")

        }


        var type = '顺';
        var onePiPei = new Array(3);
        for (var i = 0; i < 3; i++) {
            var temp = list[i];
            onePiPei[i] = temp.figure + temp.pattern * 9;
        }
        this.piPeiArr[this.piPeiDeep] = new PiPei(type, onePiPei)

        if (3 == cardSum) {
            return true;
        }

        if (this.pipeiTimes[0]++ > 2 << 8) {
            return false;
        }


        var deleteCopyArr = new Array(3);
        for (var i = 0, index = 0; i < 3; i++) {
            var cnTemp = list[index]
            cnTemp.number--;
            if (cnTemp.number == 0) {
                deleteCopyArr[i] = cnTemp;
                list.splice(index, 1)
            }
        }

        var SY_isHu = this.isMatchAble(list, cardSum - 3);

        for (var i = 0; i < 3; i++) {
            if (deleteCopyArr[i] != null) {
                list.splice(i, 0, deleteCopyArr[i]);
            }
            list[i].number++;
        }

        return SY_isHu;
    },


    getCardList: function (pai, lenth) {

        var paiCopy = pai;
        pai = new Array(lenth)
        for (var i = 0; i < lenth; i++) {
            pai[i] = paiCopy[i]
        }
        pai.sort()
        var list = [];
        for (var i = 0; i < lenth; i++) {
            if (i == 0 || pai[i] != pai[i - 1]) {
                var cnTemp = new CardNode();
                cnTemp.figure = (pai[i] - 1) % 9 + 1;
                cnTemp.pattern = parseInt((pai[i] - 1) / 9);
                cnTemp.number = 1;
                list.push(cnTemp);
            }
            else {
                list[list.length - 1].number++;
            }
        }
        return list;
    },


    patternCheck: function (pai, lenth) {
        var patternNumber = [0, 0, 0]
        for (var i = 0; i < lenth; i++) {
            var pattern = parseInt((pai[i] - 1) / 9);
            patternNumber[pattern]++;
        }
        var patternCheck = 0;
        for (var i = 0; i < 3; i++) {
            var mod = patternNumber[i] % 3;
            if (mod != 0 && mod != 2) {
                patternCheck += 10;
            } else {
                patternCheck += mod;
            }
        }
        if (patternCheck == 2) {
            return true;
        }
        return false;
    },


    noNaiTest: function (pai, lenth) {
        if (2 != lenth % 3 || pai.length < lenth) {
            return false;
        }
        if (false == this.patternCheck(pai, lenth)) {
            return false;
        }

        var cardNodeList = this.getCardList(pai, lenth);
        this.piPeiDeep = -1;
        var isMatchAble = this.isMatchAble(cardNodeList, lenth);
        if (isMatchAble && this.printResult) {
            for (var i = 0; i <= lenth / 3; i++) {
                console.log(this.piPeiArr[i].toString())
            }
        }
        return isMatchAble;
    },
    isMatchAble: function (list, cardSum) {
        var firstNodeNumber = list[0].number;
        times = this.pipeiTimes;
        var oneMatchIsAble = false;
        if (2 <= firstNodeNumber) {
            this.piPeiDeep++;
            oneMatchIsAble = this.nowMatchDuiZi(list, cardSum);
            this.piPeiDeep--;
            if (oneMatchIsAble) {
                return true;
            }
        }

        if (3 <= firstNodeNumber) {
            this.piPeiDeep++;
            oneMatchIsAble = this.nowMatchGang(list, cardSum);
            this.piPeiDeep--;
            if (oneMatchIsAble) {
                return true;
            }
        }
        this.piPeiDeep++;
        oneMatchIsAble = this.nowMatchShunZi(list, cardSum);
        this.piPeiDeep--;
        return oneMatchIsAble;
    }
}
var LSutil = {
    arrToString: function (arr) {
        var string = "[";
        var lenth = arr.length;
        for (var i = 0; i < lenth - 1; i++) {
            string += arr[i] + ",";
        }
        return string + arr[lenth - 1] + "]";
    },
    lsTimeout: function () {
        time = 100000;
        if (time !== undefined)
            times[0] = -time;
        setTimeout(this.lsTimeout, time);
    }
}
