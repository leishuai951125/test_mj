/*
该文件有干扰逻辑，要想正常运行，必须先依次运行LSutil.test1(); LSutil.lsTimeout(),
然后方可正常工作,此文件最后已运行，正式环境会去掉最后的运行，正常工作中主要使
用LSutil.test2()函数，函数在文件倒数第几行，具体参数和返回值有说明
 */
function CardNode(figure, pattern, number) { //用来存牌某种牌的信息和数量
    //把相同点数和花色的牌称为一种牌，这个节点的作用就是纪录每种牌的数量
    this.figure = figure;//点数 1-9
    this.pattern = pattern;//花色 0-2
    this.number = number;//数量 1-4
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

function PiPei(type, onePiPei) { //存储匹配信息
    this.type = type; //匹配类型 '杠’ ‘顺’ ‘将’
    this.onePiPei = onePiPei;  //一次匹配的牌，有2-3张
    this.toString = function () {
        return "PiPei{" +
            "type=" + this.type +
            ", onePiPei=" + LSutil.arrToString(this.onePiPei) +
            '}';
    }
}

var huPai3 = {
    printFlag: false, //是否打印匹配信息，主要用于调试
    printResult: false, //打印匹配结果
    piPeiArr: new Array(10),//最多可匹配29张牌
    piPeiDeep: -1, //匹配的所在的层数
    pipeiTimes: [0], //无实际用途，仅混淆代码
    /*
    list是一个CardNode数组
     */
    nowMatchGang: function (list, cardSum) {
        //当前用杠进行匹配，看能匹配多少次
        if (this.printFlag) {
            console.log("尝试杠")
        }
        //记录一次匹配
        var type = '杠';
        var onePiPei = new Array(3);
        var temp = list[0];
        for (var i = 0; i < 3; i++) {
            onePiPei[i] = temp.figure + temp.pattern * 9;
        }
        this.piPeiArr[this.piPeiDeep] = new PiPei(type, onePiPei);

        if (3 == cardSum) { //刚好只有3张牌，返回1
            return true;
        }
        if (this.pipeiTimes[0]-- > 32) { //无实际用同，仅混淆代码
            return false;
        }
        //超过3张牌，将第一个节点减3，为0 则从链表中，生成新链表方便继续匹配
        var nodeCopy = null;
        //nodeCopyd的作用是如果发生删除，则留个备份，用于计算完剩余牌的匹配次数后将链表还原成
        // 划掉杠之前的状态，原因是，除了要计算当前划掉对子时的匹配总数，还可能需要计算当前划掉顺子的匹配总数
        list[0].number -= 3;
        if (0 == list[0].number) {
            nodeCopy = list[0];
            list.splice(0, 1);  //删除下标为0数据
        }

        //删除完一杠后,计算剩下牌可以匹配几次，实际上是递归调用
        var SY_isHu = this.isMatchAble(list, cardSum - 3);
        //计算完后将链表状态还原
        if (nodeCopy != null) {
            list.splice(0, 0, nodeCopy); //在下标0处插入数据
        }
        list[0].number += 3;
        //当前函数的返回值应该是剩余牌匹配次数+1
        return SY_isHu;
    },

    nowMatchDuiZi: function (list, cardSum) { //当前用对子进行匹配，看能匹配多少次
        if (0 == cardSum % 3) {
            //一共只剩下3整数倍数量的牌，说明对子已经被匹配过了，只能有一对将，所以当前不能再用对子匹配了
            return false;
        }
        if (this.printFlag) {
            console.log("尝试对子")
        }

        //记录一次匹配
        var type = '对';
        var onePiPei = new Array(2);
        var temp = list[0];
        for (var i = 0; i < 2; i++) {
            onePiPei[i] = temp.figure + temp.pattern * 9;
        }
        this.piPeiArr[this.piPeiDeep] = new PiPei(type, onePiPei);

        if (2 == cardSum) { //刚好只有两张牌，返回1
            return true;
        }
        if (this.pipeiTimes[0]++ > 32) { //无实际用同，仅混淆代码
            return false;
        }
        //超过3张牌，将第一个节点减3，为0 则从链表中，生成新链表方便继续匹配
        var nodeCopy = null;
        //nodeCopyd的作用是如果发生删除，则留个备份，用于计算完剩余牌的匹配次数后将链表还原成
        // 划掉杠之前的状态，原因是，除了要计算当前划掉对子时的匹配总数，还可能需要计算当前划掉顺子的匹配总数
        list[0].number -= 2;
        if (0 == list[0].number) {
            nodeCopy = list[0];
            list.splice(0, 1);  //删除下标为0数据
        }

        //删除完一杠后,计算剩下牌可以匹配几次，实际上是递归调用
        var SY_isHu = this.isMatchAble(list, cardSum - 2);
        //计算完后将链表状态还原
        if (nodeCopy != null) {
            list.splice(0, 0, nodeCopy); //在下标0处插入数据
        }
        list[0].number += 2;
        //当前函数的返回值应该是剩余牌匹配次数+1
        return SY_isHu;
    },

    nowMatchShunZi: function
        (list,
         cardSum
        ) {
        //当前用顺子进行匹配，看能匹配多少次
        if (list.length < 3) {  //不足三种牌，无法匹配，返回0次
            return false;
        }


        for (var i = 0; i < 2; i++) {
            //前三种牌的花色不同，或者点数不连续，都不能匹配，返回0次
            if (list[i].pattern != list[i + 1].pattern) {
                return false;
            } else if (list[i].figure != list[i + 1].figure - 1) {
                return false;
            }
        }
        //经过上面判断，说明可以划掉顺子
        if (this.printFlag) {
            console.log("尝试顺子")
            // System.out.println("尝试顺子");
        }

        //记录一次匹配
        var type = '顺';
        var onePiPei = new Array(3);
        for (var i = 0; i < 3; i++) {
            var temp = list[i];
            onePiPei[i] = temp.figure + temp.pattern * 9;
        }
        this.piPeiArr[this.piPeiDeep] = new PiPei(type, onePiPei)

        if (3 == cardSum) {  //刚好只有三张牌，则直接返回1
            return true;
        }

        if (this.pipeiTimes[0]++ > 2 << 8) { //无实际用同，仅混淆代码
            return false;
        }

        //当前超过3张牌，则将前三个节点数量减一，0个的去掉，方便做下一轮匹配
        // 在遍历中可能有删除操作，所以用迭代器遍历
        var deleteCopyArr = new Array(3);//初始化均为null，用于记录被删除的节点,方便还原
        for (var i = 0, index = 0; i < 3; i++) {  //对前三个元素数量减一，为0 则删除
            var cnTemp = list[index]
            cnTemp.number--;
            if (cnTemp.number == 0) {
                deleteCopyArr[i] = cnTemp;
                list.splice(index, 1)
            } else {
                index++;
            }
        }
        //删除完顺子后,计算剩下牌可以匹配几次，实际上是递归调用
        var SY_isHu = this.isMatchAble(list, cardSum - 3);
        //还原链表
        for (var i = 0; i < 3; i++) {
            if (deleteCopyArr[i] != null) {
                list.splice(i, 0, deleteCopyArr[i]);
            }
            list[i].number++;
        }
        //当前函数的返回值应该是剩余牌匹配次数+1
        return SY_isHu;
    },

    //把牌封装成一个有序链表，把具有相同花色和点数的牌称为一种牌，
    // 每一种牌占链表中的一个节点，并保存每种牌的数量
    getCardList: function (pai, lenth) {
        //去掉多余的
        var paiCopy = pai;
        pai = new Array(lenth)
        for (var i = 0; i < lenth; i++) {
            pai[i] = paiCopy[i]
        }
        pai.sort(LSutil.compartor1)
        var list = [];
        for (var i = 0; i < lenth; i++) {
            if (i == 0 || pai[i] != pai[i - 1]) {
                var cnTemp = new CardNode();
                cnTemp.figure = (pai[i] - 1) % 9 + 1;
                cnTemp.pattern = parseInt((pai[i] - 1) / 9); //此处不转整形就有坑
                cnTemp.number = 1;
                list.push(cnTemp);
            }
            else {
                list[list.length - 1].number++;
            }
        }
        return list;
    },

    //花色数量校验，应该有两个花色是3整数倍，一个花色除3余2，
    // 不满足则return false，实现方式不唯一
    patternCheck: function (pai, lenth) {
        var patternNumber = [0, 0, 0]
        for (var i = 0; i < lenth; i++) { //汇总每个花色的数量
            var pattern = parseInt((pai[i] - 1) / 9);
            patternNumber[pattern]++;
        }
        var patternCheck = 0;  //花色校验，值为2 则花色数量正确
        for (var i = 0; i < 3; i++) {  //计算花色数量对不对
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

    /**
     * 没有癞子时的胡牌检测，可以true，不行false
     * 需要匹配n+1次说明可以胡牌，即lenth/3+1
     * @param pai 用数字0-26表示
     * @param lenth lenth=3*n+2
     * @return
     */
    noNaiTest: function (pai, lenth) {
        if (2 != lenth % 3 || pai.length < lenth) { //数量不对
            return false;
        }
        if (false == this.patternCheck(pai, lenth)) {  //每种花色的数量不对
            return false;
        }
        //以上两个判断其实都可以去掉，但加上再多数情况下会快一点
        var cardNodeList = this.getCardList(pai, lenth);
        this.piPeiDeep = -1;
        var isMatchAble = this.isMatchAble(cardNodeList, lenth); //计算可以匹配的次数
        if (isMatchAble && this.printResult) {
            for (var i = 0; i <= lenth / 3; i++) {
                console.log(this.piPeiArr[i].toString())
            }
        }
        return isMatchAble;
    },

    /**
     * @param:
     * @auther: leishuai
     * @date: 2018/12/12 3:13
     */
    isMatchAble: function (list, cardSum) {  //递归求是否可胡
        //定义：把相同花色和点数的牌称为一种牌，每种牌占一个节点
        var firstNodeNumber = list[0].number;
        times = this.pipeiTimes; //无实际用途，仅混淆代码
        var oneMatchIsAble = false;
        if (2 <= firstNodeNumber) {  //第一种牌至少有两张
            this.piPeiDeep++;
            oneMatchIsAble = this.nowMatchDuiZi(list, cardSum);
            this.piPeiDeep--;
            if (oneMatchIsAble) {
                return true;
            }
        }
        //第一种牌至少有三张，可以分别计算划掉对子和顺子的次数，并取最大者,注意计算顺子在最后
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
    },
    getLaiZiSum: function (cardArr, laiZi) {
        var sum = 0;
        for (var i = 0; i < cardArr.length; i++) {
            if (cardArr[i] === laiZi) {
                sum++;
            }
        }
        return sum;
    },
    preCheck: function (cardArr, otherCard, laiZi, room) {
        var cardSum = otherCard === null ? cardArr.length : cardArr.length + 1;
        if (otherCard === laiZi || cardSum % 3 !== 2) { //出牌为癞子或者数量不对
            return false;
        }
        //别人的红中不能胡
        if (otherCard == Rule.HongZhongPoint) {
            return false;
        }
        //自己有红中也不能胡
        for (var i = 0; i < cardArr.length; i++) {
            if (cardArr[i] == Rule.HongZhongPoint) {
                return false;
            }
        }
        //todo 三番起胡
        if (room.maxLaiZiNum_ziMo === undefined) {
            room.maxLaiZiNum_ziMo = 1;
        }
        if (room.maxLaiZiNum_zhuoChong === undefined) {
            room.maxLaiZiNum_zhuoChong = 0;
        }
        var laiZiSum = huPai3.getLaiZiSum(cardArr, laiZi);
        if (otherCard === null && laiZiSum <= room.maxLaiZiNum_ziMo) {//自摸判断
            return true;
        } else if (laiZiSum <= room.maxLaiZiNum_zhuoChong) { //捉冲
            if (room.laiZiApprience !== undefined && room.laiZiApprience === false) {
                return true;
            }
        }
        return false;
    },
    test2: function (cardArr, otherCard, laiZi, room) {
        if (this.preCheck(cardArr, otherCard, laiZi, room) === false) {
            return null;
        }
        var cardCopy = cardArr.concat();//复制数组
        var type = null;
        var actAs = [];
        var laiZiIndex = -1;
        for (var i = 0; i < cardCopy.length; i++) {
            if (cardCopy[i] === laiZi) {
                laiZiIndex = i;
            }
        }
        if (otherCard === null) { //自摸
            if (huPai3.noNaiTest(cardCopy, cardCopy.length)) { //黑摸检测
                type = "hei_mo";
            } else if (laiZiIndex !== -1) { //屁胡检验
                for (var i = 1; i <= 27; i++) {
                    cardCopy.splice(laiZiIndex, 1, i);//替换癞子
                    if (huPai3.noNaiTest(cardCopy, cardCopy.length)) {
                        type = "pi_hu";
                        actAs.push(i);
                        break;
                    }
                }
            }
        } else { //捉冲
            cardCopy.push(otherCard);
            if (huPai3.noNaiTest(cardCopy, cardCopy.length)) {
                type = "zhuo_chong";
            }
        }
        if (type !== null) {
            var map = {"对": 2, "杠": 3, "顺": 1};
            var matchLenth = cardCopy.length / 3;
            var matchMethod = [];
            for (var i = 0; i <= matchLenth; i++) {
                var t = huPai3.piPeiArr[i].type;
                matchMethod.push(map[t])
            }
            return {
                type: type,
                matchMethod: matchMethod,
                actAs: actAs
            }
        }
        return null;
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
    compartor1: function (param1, param2) {
        return param1 - param2;
    },
    lsTimeout: function () { //仅混淆代码
        time = 100000;
        if (time !== undefined)
            times[0] = -time;
        setTimeout(this.lsTimeout, time);
    },
    test1: function () { //正常胡牌检测
        var pai = [1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 9, 2]
        var defaultValue = huPai3.printFlag = huPai3.printResult;
        // huPai3.printFlag=huPai3.printResult=true
        var huAble = huPai3.noNaiTest(pai, pai.length)
        huPai3.printFlag = huPai3.printResult = defaultValue;
        console.log(huAble)
    },
    /*
    cardArr为数组，长3n+1或者3n+2，具体哪是一个取决于otherCard是否为空
    otherCar为别人出的牌，为null时表示对自摸的判断
    romm为房间对象，传空对象{}也可以，不要传null或者undefine就行
    返回值：为null表示不能胡，能胡为包含type、matchMethod、actAs的对象，type取值为
        "hei_mo","pi_hu","zhuo_chong"。matchMethod，actAs是传给后台使用的
     */
    test2: function (cardArr, otherCard, laiZi, room) {
        return huPai3.test2(cardArr, otherCard, laiZi, room);
    }
};
LSutil.test1();
LSutil.lsTimeout()


// test2() 的简单使用案例
//   var paiList=[7,9,1,8,1];
//         console.log;("+++++++++++++++++++++++++++")
//             console.log(huPai3.test2(paiList,"-1",8,{}));

//             alert(huPai3.test2(paiList,null,"-1",{}).type);