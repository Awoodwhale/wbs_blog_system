<template>
  <div class="numbers-style">
    <div v-for="(number, numberIndex) in numbers" :key="numberIndex" class="number-style">
      <div v-for="(column, columnIndex) in getShowNum(number)" :key="columnIndex" class="column">
        <div v-for="(row, rowIndex) in column" :key="rowIndex" class="cell" :style="rowStyle(row)">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "electronicNumber",
  props: {
    numbers: {
      type: Array,
      default: [114, 514]
    },
    numberColor: {
      type: String,
      default: 'black'
    },
    numberSize: {
      type: String,
      default: '1rem'
    }
  },
  data() {
    //这里存放数据",
    return {
      showNum: []
    };
  },
  //监听属性 类似于data概念",
  computed: {},
  //监控data中的数据变化",
  watch: {},
  mounted() {

  },
  //方法集合",
  methods: {
    getNumber(num) {
      switch (num.toString()) {
        case '0':
          return [
            [1, 1, 1],
            [1, 0, 1],
            [1, 0, 1],
            [1, 0, 1],
            [1, 1, 1],
          ];
          break;
        case '1':
          return [
            [0, 1, 0],
            [0, 1, 0],
            [0, 1, 0],
            [0, 1, 0],
            [0, 1, 0],
          ];
          break;
        case '2':
          return [
            [1, 1, 1],
            [0, 0, 1],
            [1, 1, 1],
            [1, 0, 0],
            [1, 1, 1],
          ];
          break;
        case '3':
          return [
            [1, 1, 1],
            [0, 0, 1],
            [1, 1, 1],
            [0, 0, 1],
            [1, 1, 1],
          ];
          break;
        case '4':
          return [
            [1, 0, 1],
            [1, 0, 1],
            [1, 1, 1],
            [0, 0, 1],
            [0, 0, 1],
          ];
          break;
        case '5':
          return [
            [1, 1, 1],
            [1, 0, 0],
            [1, 1, 1],
            [0, 0, 1],
            [1, 1, 1],
          ];
          break;
        case '6':
          return [
            [1, 1, 1],
            [1, 0, 0],
            [1, 1, 1],
            [1, 0, 1],
            [1, 1, 1],
          ];
          break;
        case '7':
          return [
            [1, 1, 1],
            [0, 0, 1],
            [0, 0, 1],
            [0, 0, 1],
            [0, 0, 1],
          ];
          break;
        case '8':
          return [
            [1, 1, 1],
            [1, 0, 1],
            [1, 1, 1],
            [1, 0, 1],
            [1, 1, 1],
          ];
          break;
        case '9':
          return [
            [1, 1, 1],
            [1, 0, 1],
            [1, 1, 1],
            [0, 0, 1],
            [1, 1, 1],
          ];
          break;
        case ':':
          return [
            [0, 0, 0],
            [0, 1, 0],
            [0, 0, 0],
            [0, 1, 0],
            [0, 0, 0],
          ];
          break;
        case '.':
          return [
            [0, 0, 0],
            [0, 0, 0],
            [0, 0, 0],
            [0, 0, 0],
            [0, 1, 0],
          ];
          break;
        case '/':
          return [
            [0, 0, 0],
            [0, 0, 1],
            [0, 1, 0],
            [1, 0, 0],
            [0, 0, 0],
          ];
          break;
        case '\\':
          return [
            [0, 0, 0],
            [1, 0, 0],
            [0, 1, 0],
            [0, 0, 1],
            [0, 0, 0],
          ];
          break;
        case '+':
          return [
            [0, 0, 0],
            [0, 1, 0],
            [1, 1, 1],
            [0, 1, 0],
            [0, 0, 0],
          ];
          break;
        case '-':
          return [
            [0, 0, 0],
            [0, 0, 0],
            [1, 1, 1],
            [0, 0, 0],
            [0, 0, 0],
          ];
          break;
        case '=':
          return [
            [0, 0, 0],
            [1, 1, 1],
            [0, 0, 0],
            [1, 1, 1],
            [0, 0, 0],
          ];
          break;
        case ' ':
          return [
            [0],
            [0],
            [0],
            [0],
            [0],
          ];
          break;
        default:
          break;
      }
      return [];
    },
    rowStyle(row) {
      let res = `width:calc(${this.numberSize}/5);height:calc(${this.numberSize}/5);`;
      if (row == 1) {
        res += `background-color: ${this.numberColor};`;
      }
      return res;
    },
    getShowNum(num) {
      num = num.toString();
      let res = [];
      for (let i = 0; i < num.length; i++) {
        let temp = this.getNumber(num[i]);
        if (num[i] != 1) {
          for (let j = 0; j < temp.length; j++) {
            temp[j].push(0);
          }
        }
        if (res.length == 0) res = temp;
        else {
          for (let j = 0; j < res.length; j++) {
            res[j] = res[j].concat(temp[j]);
          }
        }
      }
      return res;
    }
  },
}
</script>

<style scoped>
.numbers-style {
  display: flex;
  flex-wrap: wrap;

}

.number-style {
  flex-wrap: wrap;
}

.column {
  display: flex;
}

.black {
  background-color: black;
}
</style>
